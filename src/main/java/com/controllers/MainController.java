package com.controllers;

import com.Entity.ForumUser;
import com.Entity.MailMessage;
import com.Entity.Sector;
import com.dao.BoardDao;
import com.service.AuthService;
import com.service.SectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Controller
public class MainController {

    @Autowired
    private AuthService authService;

    @Autowired
    private SectorService sectorService;

    @Autowired
    private BoardDao boardDao;

    @GetMapping("/")
    public String getMainPage(HttpServletRequest request, Model model, HttpServletResponse response) {
        Cookie locationCookie = new Cookie("location", "/");
        locationCookie.setPath("/");
        response.addCookie(locationCookie);

        HttpSession session = request.getSession();
        ForumUser forumUser = authService.getForumUserBySessionId(session.getId());

        Set<Sector> sectors = sectorService.getAll();

        Map<String, Object> mapModel = new HashMap<>();
        mapModel.put("forum_user", forumUser);
        mapModel.put("sectors", sectorService.getAll());
        Integer totalUnreadCount = 0;
        if (forumUser != null) {
            for (MailMessage mailMessage : forumUser.getReceivedLetters()) {
                if (!mailMessage.getIsRead()) totalUnreadCount++;
            }
        }
        mapModel.put("totalUnreadCount", totalUnreadCount);

        model.addAllAttributes(mapModel);
        return "index";
    }
}
