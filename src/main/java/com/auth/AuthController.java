package com.auth;

import com.Entity.Gender;
import com.dao.ForumUserDao;
import com.service.AuthService;
import com.tools.SaltGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {

    @Autowired
    ForumUserDao forumUserDao;

    @Autowired
    private AuthService authService;

    @Autowired
    private SaltGenerator saltGenerator;

    @GetMapping("/login")
    public String getLoginPage(HttpServletRequest request, Model model) {
        String sessionId = request.getSession().getId();
        if (authService.isContainsSessionId(sessionId)) return "redirect:/"; //Если уже залогинен, то возвращаем на главную.
        String ip = request.getRemoteAddr();
        Map<String, Object> mapModel = new HashMap<>();
        String salt = saltGenerator.getRandomSalt();
        mapModel.put("salt", salt);
        authService.singForm(sessionId, ip, salt);

        model.addAllAttributes(mapModel);
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, @RequestParam String salt, HttpServletRequest request, Model model) {
        String sessionId = request.getSession().getId();
        String ip = request.getRemoteAddr();
        try {
            authService.login(email, password, sessionId, ip, salt);
        } catch (RuntimeException e) {
            String newSalt = saltGenerator.getRandomSalt();
            authService.singForm(sessionId, ip, newSalt);
            model.addAttribute("salt", newSalt);
            model.addAttribute("reason", e.getMessage());
            return "login";
        }

        String from = null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("location")) {
                from = cookie.getValue();
            }
        }

        return from == null ? "redirect:/" : "redirect:" + from;
    }

    @GetMapping("/registration")
    public String getRegistrationPage(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        if (authService.isContainsSessionId(session.getId())) return "redirect:/"; //Если уже залогинен, то возвращаем на главную.

        Cookie locationCookie = new Cookie("location", "/");
        locationCookie.setPath("/");
        response.addCookie(locationCookie);

        if (authService.isContainsSessionId(session.getId())) return "redirect:"; //если уже залогинен, то возвращаем на главную.
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@RequestParam(required = true) String nickname,
                               @RequestParam(required = false, name = "year-of-birth") String yearOfBirth,
                               @RequestParam(required = false, name = "month-of-birth") String monthOfBirth,
                               @RequestParam(required = false, name = "day-of-birth") String dayOfBirth,
                               @RequestParam(required = true) String password,
                               @RequestParam(required = true) String repassword,
                               @RequestParam(required = true) String email,
                               @RequestParam(required = false) String gender, Model model,
                               HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        String dob = null;
        if ((yearOfBirth != null && !yearOfBirth.isEmpty()) && (monthOfBirth != null && !monthOfBirth.isEmpty()) && (dayOfBirth != null && !dayOfBirth.isEmpty())) {
            dob = (Integer.parseInt(dayOfBirth) + 1) + "." + monthOfBirth + "." + yearOfBirth;
        }
        Date dateOfBirth = new Date();
        if (dob != null){
            if (!dob.isEmpty()){
                try {
                    dateOfBirth = new SimpleDateFormat("dd.MM.yyyy").parse(dob);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(dateOfBirth);

        Gender regGender = Gender.UNKNOWN;
        if (gender != null) {
            if (!gender.isEmpty()) {
                regGender = gender.equals("male") ? Gender.MALE : Gender.FEMALE;
            }
        }

        try {
            authService.registerNewForumUser(nickname, dateOfBirth, password, repassword, email, regGender, request.getScheme(), request.getServerName());
            modelMap.put("msg", "Теперь Вы можете войти");
            String sessionId = request.getSession().getId();
            String ip = request.getRemoteAddr();
            String salt = saltGenerator.getRandomSalt();
            authService.singForm(sessionId, ip, salt);
            modelMap.put("salt", salt);
            model.addAllAttributes(modelMap);
        } catch (RuntimeException | IOException e) {
            modelMap.put("reason", e.getMessage());
            model.addAllAttributes(modelMap);
            return "registration";
        }

        return "login";
    }

    @GetMapping("/logout")
    public String loguot(HttpServletRequest request) {
        HttpSession session = request.getSession();
        authService.logout(session.getId());

        String from = null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("location")) {
                from = cookie.getValue();
            }
        }

        return from == null ? "redirect:/" : "redirect:" + from;
    }
}
