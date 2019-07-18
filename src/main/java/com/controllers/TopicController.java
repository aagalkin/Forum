package com.controllers;

import com.Entity.*;
import com.dao.BoardDao;
import com.dao.TopicDao;
import com.service.AuthService;
import com.service.SectorService;
import com.service.TopicService;
import com.tools.Counter;
import com.tools.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.*;

@Controller
@RequestMapping("/topic")
public class TopicController {

    @Autowired
    private TopicService topicService;

    @Autowired
    private AuthService authService;

    @Autowired
    private SectorService sectorService;

    @Autowired
    private TopicDao topicDao;

    @Autowired
    private BoardDao boardDao;

    @GetMapping("/create")
    public String creatingPage(HttpServletRequest request, Model model, @RequestParam(name = "board") Long boardId) {
        HttpSession session = request.getSession();
        ForumUser forumUser = authService.getForumUserBySessionId(session.getId());
        if (forumUser == null) return "redirect:/login";
        Map<String, Object> mapModel = new HashMap<>();
        mapModel.put("forum_user", forumUser);
        Board board = boardDao.findById(boardId).orElse(null);
        if (board == null) return "redirect:/board/" + boardId + "/1";
        mapModel.put("board", board);
        Integer totalUnreadCount = 0;
        if (forumUser != null) {
            for (MailMessage mailMessage : forumUser.getReceivedLetters()) {
                if (!mailMessage.getIsRead()) totalUnreadCount++;
            }
        }
        mapModel.put("totalUnreadCount", totalUnreadCount);
        model.addAllAttributes(mapModel);

        return "pretopic";
    }

    @PostMapping("/create")
    public String create(@RequestParam String name, @RequestParam String text,
                         @RequestParam String desc, @RequestParam Long authorId,
                         @RequestParam Long boardId) {
        Long topicId = topicService.create(name, desc, text, authorId, boardId);
        return "redirect:/topic/" + topicId + "/1";
    }

    @GetMapping("/{id}/{page}")
    public String goTo(@PathVariable Long id, @PathVariable Integer page, HttpServletRequest request, Model model, HttpServletResponse response) {
        Cookie locationCookie = new Cookie("location", "/topic/" + id + "/" + page);
        locationCookie.setPath("/");
        response.addCookie(locationCookie);

        HttpSession session = request.getSession();
        System.out.println(session.getId());
        ForumUser forumUser = authService.getForumUserBySessionId(session.getId());

        Map<String, Object> mapModel = new HashMap<>();

        Topic topic = topicDao.findById(id).orElse(null);
        if (topic == null) return "redirect:/";

        Integer messagesOnPage = 10;

        List<ForumMessage> forumMessages = new ArrayList<>(topic.getForumMessages());
        List<ForumMessage> forumMessageOnPageList = new ArrayList<>();
        for (int i = ((page-1) * messagesOnPage); i < (page * messagesOnPage); i++) {
            try {
                forumMessageOnPageList.add(forumMessages.get(i));
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }

        Integer lastPage = (forumMessages.size() / messagesOnPage) + (forumMessages.size() % messagesOnPage == 0 ? 0 : 1);

        Pagination pagination = new Pagination(page, lastPage);

        Counter counter = new Counter((page - 1) * messagesOnPage);

        mapModel.put("pagination", pagination);
        mapModel.put("counter", counter);
        mapModel.put("forum_user", forumUser);
        mapModel.put("topic", topic);
        mapModel.put("forum_messages", forumMessageOnPageList);
        Integer totalUnreadCount = 0;
        if (forumUser != null) {
            for (MailMessage mailMessage : forumUser.getReceivedLetters()) {
                if (!mailMessage.getIsRead()) totalUnreadCount++;
            }
        }
        mapModel.put("totalUnreadCount", totalUnreadCount);
        model.addAllAttributes(mapModel);

        return "topic";
    }
}