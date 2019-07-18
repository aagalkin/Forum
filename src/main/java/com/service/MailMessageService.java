package com.service;

import com.Entity.ForumUser;
import com.Entity.MailMessage;
import com.Factories.MailMessageFactory;
import com.dao.ForumUserDao;
import com.dao.MailMessageDao;
import com.tools.Security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class MailMessageService {

    @Autowired
    private ForumUserDao forumUserDao;

    @Autowired
    private MailMessageFactory mailMessageFactory;

    @Autowired
    private MailMessageDao mailMessageDao;

    @Autowired
    private Security security;

    public void sendMessage(ForumUser sender, String recipientName, String theme, String text) {
        ForumUser recipient = forumUserDao.findByNickname(recipientName).orElse(null);
        if (recipient == null) {
            throw new RuntimeException("Пользователь с таким именем не найден");
        }

        MailMessage mailMessage = mailMessageFactory.createNewMessage();
        mailMessage.setDate(new Date());
        mailMessage.setTheme(theme);
        mailMessage.setText(security.xssSecurity(text));
        mailMessage.setSender(sender);
        mailMessage.setRecipient(recipient);
        mailMessage.setIsRead(false);
        mailMessageDao.save(mailMessage);
    }

    public List<ForumUser> sortContactsByDate(List<ForumUser> contacts, ForumUser forumUser) {
        List<ForumUser> result = new ArrayList<>();
        List<MailMessage> messages = new ArrayList<>();
        for (int i = 0; i < contacts.size(); i++) {
            List<MailMessage> msgs = new ArrayList<>();
            for (MailMessage mailMessage : contacts.get(i).getSentLetters()) {
                if (mailMessage.getRecipient().equals(forumUser)) {
                    msgs.add(mailMessage);
                }
            }
            for (MailMessage mailMessage : contacts.get(i).getReceivedLetters()) {
                if (mailMessage.getSender().equals(forumUser)) {
                    msgs.add(mailMessage);
                }
            }
            Collections.sort(msgs);
            messages.add(msgs.get(0));
        }
        Collections.sort(messages);
        for (MailMessage mailMessage : messages) {
            result.add(mailMessage.getRecipient().equals(forumUser) ? mailMessage.getSender() : mailMessage.getRecipient());
        }
        return result;
    }
}
