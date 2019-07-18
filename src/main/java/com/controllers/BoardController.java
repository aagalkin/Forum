package com.controllers;

import com.Entity.Board;
import com.Entity.ForumUser;
import com.Entity.MailMessage;
import com.Entity.Topic;
import com.dao.BoardDao;
import com.service.AuthService;
import com.service.BoardService;
import com.tools.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardDao boardDao;

    @Autowired
    private AuthService authService;

    @PostMapping("/create")
    public String create(@RequestParam String name, @RequestParam String desc, @RequestParam Long sectorId) {
        boardService.create(name, desc, sectorId);
        return "redirect:/";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id) {
        boardService.delete(id);
        return "redirect:/";
    }

    @GetMapping("/{id}/{page}")
    public String goTo(@PathVariable Long id, Model model,
                       @PathVariable Integer page, HttpServletRequest request, HttpServletResponse response) {
        Cookie locationCookie = new Cookie("location", "/board/" + id + "/" + page);
        locationCookie.setPath("/");
        response.addCookie(locationCookie);

        HttpSession session = request.getSession();
        ForumUser forumUser = authService.getForumUserBySessionId(session.getId());
        Map<String, Object> mapModel = new HashMap<>();
        mapModel.put("forum_user", forumUser);
        Integer totalUnreadCount = 0;
        if (forumUser != null) {
            for (MailMessage mailMessage : forumUser.getReceivedLetters()) {
                if (!mailMessage.getIsRead()) totalUnreadCount++;
            }
        }
        mapModel.put("totalUnreadCount", totalUnreadCount);
        Board board = boardDao.findById(id).orElse(null);
        mapModel.put("board", board);

        Integer topicsOnPage = 20;

        List<Topic> topics = new ArrayList<>(board.getTopics());
        List<Topic> topicsOnPageList = new ArrayList<>();
        for (int i = ((page-1) * topicsOnPage); i < (page * topicsOnPage); i++) {
            try {
                topicsOnPageList.add(topics.get(i));
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }

        Integer lastPage = (topics.size() / topicsOnPage) + (topics.size() % topicsOnPage == 0 ? 0 : 1);

        Pagination pagination = new Pagination(page, lastPage);

        mapModel.put("pagination", pagination);
        mapModel.put("topics", topicsOnPageList);

        model.addAllAttributes(mapModel);
        return "board";
    }
}
