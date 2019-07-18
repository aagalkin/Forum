package com.controllers;

import com.Entity.ForumUser;
import com.Entity.MailMessage;
import com.dao.ForumUserDao;
import com.dao.MailMessageDao;
import com.service.AuthService;
import com.service.MailMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("/mail")
public class MailController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ForumUserDao forumUserDao;

    @Autowired
    private MailMessageService mailMessageService;

    @Autowired
    MailMessageDao mailMessageDao;

    @GetMapping()
    public String getMailPage (HttpServletRequest request, Model model, @RequestParam(required = false) Long contactId) {
        HttpSession session = request.getSession();
        ForumUser forumUser = authService.getForumUserBySessionId(session.getId());
        if (forumUser == null) return "redirect:/login"; //Если не залогиненный, то кидает на страницу входа

        Map<String, Object> mapModel = new HashMap<>();
        mapModel.put("forum_user", forumUser);

        List<ForumUser> contacts = new ArrayList<>();
        for (int i = 0; i < forumUser.getSentLetters().size(); i++) {
            ForumUser recipient = forumUser.getSentLetters().get(i).getRecipient();
            if (!contacts.contains(recipient)) contacts.add(recipient);
        }
        for (int i = 0; i < forumUser.getReceivedLetters().size(); i++) {
            ForumUser sender = forumUser.getReceivedLetters().get(i).getSender();
            if (!contacts.contains(sender)) contacts.add(sender);
        }

        contacts = mailMessageService.sortContactsByDate(contacts, forumUser);
        mapModel.put("contacts", contacts);

        ForumUser targetContact = null;
        List<MailMessage> messages = new ArrayList<>();

        Integer totalUnreadCount = 0;
        if (forumUser != null) {
            for (MailMessage mailMessage : forumUser.getReceivedLetters()) {
                if (!mailMessage.getIsRead()) totalUnreadCount++;
            }
        }
        mapModel.put("totalUnreadCount", totalUnreadCount);

        if (contacts.size() == 0) {
            mapModel.put("target", null);
            mapModel.put("messages", messages);
            return "mail";
        }

        if (contactId != null) {
            targetContact = forumUserDao.findById(contactId).orElse(null);
        }
        if (targetContact == null || !contacts.contains(targetContact)) {
            targetContact = contacts.get(0);
        }

        mapModel.put("target", targetContact);

        if (targetContact != null) {
            for (int i = 0; i < forumUser.getSentLetters().size(); i++) {
                ForumUser contact = forumUser.getSentLetters().get(i).getRecipient();
                if (contact.equals(targetContact)) messages.add(forumUser.getSentLetters().get(i));
            }
            for (int i = 0; i < forumUser.getReceivedLetters().size(); i++) {
                ForumUser contact = forumUser.getReceivedLetters().get(i).getSender();
                if (contact.equals(targetContact)) messages.add(forumUser.getReceivedLetters().get(i));
            }
        }
        Collections.sort(messages);

        mapModel.put("messages", messages);

        UnreadCounter counter = new UnreadCounter(forumUser);
        mapModel.put("counter", counter);

        model.addAllAttributes(mapModel);

        return "mail";
    }

    @PostMapping("/send")
    public String sendMessage(HttpServletRequest request, Model model,
                              @RequestParam(name = "recipient") String recipientName,
                              @RequestParam String theme, @RequestParam String text) {
        HttpSession session = request.getSession();
        ForumUser forumUser = authService.getForumUserBySessionId(session.getId());
        if (forumUser == null) return "redirect:/login"; //Если не залогиненный, то кидает на страницу входа


        Map<String, Object> mapModel = new HashMap<>();
        try {
            mailMessageService.sendMessage(forumUser, recipientName, theme, text);
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }

        return "redirect:/mail";
    }

    @GetMapping("/send")
    public String getPremailPage(HttpServletRequest request, Model model,
                                 @RequestParam(required = false) String recipient,
                                 @RequestParam(required = false) String theme,
                                 @RequestParam(required = false) String text) {
        HttpSession session = request.getSession();
        ForumUser forumUser = authService.getForumUserBySessionId(session.getId());
        if (forumUser == null) return "redirect:/login"; //Если не залогиненный, то кидает на страницу входа

        model.addAttribute("forum_user", forumUser);

        if (recipient != null) model.addAttribute("recipient", recipient);
        if (theme !=  null && text != null) {
            model.addAttribute("theme", theme.substring(0, 4).equals("RE: ") ? theme : "RE: " + theme);
            model.addAttribute("text", text);
        }

        Integer totalUnreadCount = 0;
        if (forumUser != null) {
            for (MailMessage mailMessage : forumUser.getReceivedLetters()) {
                if (!mailMessage.getIsRead()) totalUnreadCount++;
            }
        }
        model.addAttribute("totalUnreadCount", totalUnreadCount);

        return "premail";
    }

    @GetMapping("/letter")
    public String getLetter(HttpServletRequest request, Model model,
                            @RequestParam Long id) {
        HttpSession session = request.getSession();
        ForumUser forumUser = authService.getForumUserBySessionId(session.getId());
        if (forumUser == null) return "redirect:/login"; //Если не залогиненный, то кидает на страницу входа

        MailMessage mailMessage = mailMessageDao.findById(id).orElse(null);
        if (mailMessage == null) return "404"; //TODO

        if (!mailMessage.getSender().equals(forumUser) && !mailMessage.getRecipient().equals(forumUser)) {
            return "forbidden"; //TODO
        }

        if (mailMessage.getRecipient().equals(forumUser)) {
            if (!mailMessage.getIsRead()) {
                mailMessage.setIsRead(true);
                mailMessageDao.save(mailMessage);
            }
        }

        model.addAttribute("forum_user", forumUser);
        model.addAttribute("message", mailMessage);

        Integer totalUnreadCount = 0;
        if (forumUser != null) {
            for (MailMessage mailMessage1 : forumUser.getReceivedLetters()) {
                if (!mailMessage1.getIsRead()) totalUnreadCount++;
            }
        }
        model.addAttribute("totalUnreadCount", totalUnreadCount);

        return "letter";
    }

    class UnreadCounter {

        private ForumUser forumUser;

        public UnreadCounter(ForumUser forumUser) {
            this.forumUser = forumUser;
        }

        public Integer count (ForumUser contact) {
            Integer i = 0;
            for (MailMessage message : contact.getSentLetters()) {
                if (message.getRecipient().equals(forumUser)) {
                    if (!message.getIsRead()) i++;
                }
            }
            return i;
        }
    }
}
