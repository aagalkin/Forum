package com.controllers;

import com.Entity.ForumMessage;
import com.Entity.Topic;
import com.dao.ForumMessageDao;
import com.service.ForumMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/fmessage")
public class ForumMessageController {

    @Autowired
    private ForumMessageService forumMessageService;

    @Autowired
    private ForumMessageDao forumMessageDao;

    @PostMapping("/add")
    public String create(@RequestParam String text, Long authorId, Long topicId) {
        Long forumMessageId = forumMessageService.create(text, authorId, topicId);

        return "redirect:/topic/" + topicId + "/1";
    }

    @GetMapping("/goto/{id}")
    public String toGoMessage(HttpServletRequest request, HttpServletResponse response,
                              @PathVariable Long id) {
        ForumMessage forumMessage = forumMessageDao.findById(id).orElse(null);
        if (forumMessage == null) return "/";
        Topic topic = forumMessage.getTopic();
        Integer page = 0;
        Integer counter = 1;
        for (ForumMessage forumMessage1 : topic.getForumMessages()) {
            if (forumMessage.getId().equals(forumMessage1.getId())) {
                break;
            } else {
                counter++;
            }
        }
        page = counter / 10 + (counter % 10 == 0 ? 0 : 1);
        return "redirect:/topic/" + topic.getId() + "/" + page + "#" + forumMessage.getId();
    }
}
