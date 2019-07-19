package com.controllers;

import com.Entity.ForumUser;
import com.Entity.Gender;
import com.Entity.MailMessage;
import com.dao.ForumUserDao;
import com.service.AuthService;
import com.tools.SaltGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.rowset.serial.SerialBlob;
import javax.transaction.Transactional;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ForumUserController {

    @Autowired
    AuthService authService;

    @Autowired
    ForumUserDao forumUserDao;

    @Autowired
    SaltGenerator saltGenerator;

    @GetMapping("/profile")
    public String getSettingsForm(HttpServletRequest request, Model model,
                                     @RequestParam(required = false) String form) {
        HttpSession session = request.getSession();
        ForumUser forumUser = authService.getForumUserBySessionId(session.getId());
        if (forumUser == null) return "redirect:/login"; //Если не залогиненный, то кидает на страницу входа
        if(form == null || form.isEmpty()) form = "profile";


        Map<String, Object> mapModel = new HashMap<>();

        if (form.equals("security") || form.equals("email")) {
            String sessionId = request.getSession().getId();
            String ip = request.getRemoteAddr();
            String salt = saltGenerator.getRandomSalt();
            authService.singForm(sessionId, ip, salt);
            mapModel.put("salt", salt);
        }

        mapModel.put("forum_user", forumUser);
        mapModel.put("form", form);

        Integer totalUnreadCount = 0;
        if (forumUser != null) {
            for (MailMessage mailMessage : forumUser.getReceivedLetters()) {
                if (!mailMessage.getIsRead()) totalUnreadCount++;
            }
        }
        mapModel.put("totalUnreadCount", totalUnreadCount);

        model.addAllAttributes(mapModel);
        return "profile";
    }

    @GetMapping("user/dob/{id}")
    @ResponseBody
    public Date getRegDate(@PathVariable Long id) {
        ForumUser forumUser = forumUserDao.findById(id).orElse(null);
        if (forumUser == null) return null;
        System.out.println("ok!");
        return forumUser.getDateOfBirth();
    }

    @PostMapping("/user/data")
    public String editUserData(HttpServletRequest request,
                               @RequestParam String gender,
                               @RequestParam(name = "year-of-birth") String yearOfBirth,
                               @RequestParam(name = "month-of-birth") String monthOfBirth,
                               @RequestParam(name = "day-of-birth") String dayOfBirth,
                               @RequestParam String signature) throws IOException {
        HttpSession session = request.getSession();
        ForumUser forumUser = authService.getForumUserBySessionId(session.getId());
        if (forumUser == null) return "redirect:/login"; //Если не залогиненный, то кидает на страницу входа

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

        forumUser.setDateOfBirth(dateOfBirth);
        Gender gdr = null;
        if (gender.equalsIgnoreCase("male")) gdr = Gender.MALE;
        else if (gender.equalsIgnoreCase("female")) gdr = Gender.FEMALE;
        else gdr = Gender.UNKNOWN;
        forumUser.setGender(gdr);
        String imageUrl = request.getScheme() + "://" + request.getServerName() + "/avatar/default_" + forumUser.getGender().toString().toLowerCase() + ".jpg";
        byte[] bytes = getDefaultAvatarFile(imageUrl);
        if (forumUser.getDefaultAvatar()) {
            Blob blob = null;
            try {
                blob = new SerialBlob(bytes);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            forumUser.setAvatar(blob);
        }
        forumUser.setSignature(signature);

        forumUserDao.save(forumUser);


        return "redirect:/profile?form=settings";
    }

    @PostMapping("/user/changepassword")
    public String changePassword(HttpServletRequest request, Model model,
                                 @RequestParam(name = "old-password") String password,
                                 @RequestParam(name = "new-password") String newPassword,
                                 @RequestParam(name = "new-password") String renewPassword,
                                 @RequestParam String salt) {
        HttpSession session = request.getSession();
        ForumUser forumUser = authService.getForumUserBySessionId(session.getId());
        if (forumUser == null) return "redirect:/login"; //Если не залогиненный, то кидает на страницу входа

        String sessionId = request.getSession().getId();
        String ip = request.getRemoteAddr();

        Map<String, Object> mapModel = new HashMap<>();
        try{
            authService.changePassword(forumUser, password, newPassword, renewPassword, sessionId, ip, salt);
            mapModel.put("msg", "Пароль успешно изменён");
        } catch (RuntimeException e) {
            mapModel.put("msg", e.getMessage());
        }

        String newSalt = saltGenerator.getRandomSalt();
        authService.singForm(sessionId, ip, newSalt);

        String form = "security";
        mapModel.put("salt", newSalt);
        mapModel.put("forum_user", forumUser);
        mapModel.put("form", form);
        Integer totalUnreadCount = 0;
        if (forumUser != null) {
            for (MailMessage mailMessage : forumUser.getReceivedLetters()) {
                if (!mailMessage.getIsRead()) totalUnreadCount++;
            }
        }
        mapModel.put("totalUnreadCount", totalUnreadCount);
        model.addAllAttributes(mapModel);

        return "profile";
    }

    @PostMapping("/user/changeemail")
    public String changeEmail(HttpServletRequest request, Model model,
                              @RequestParam String password,
                              @RequestParam(name = "new-email") String newEmail,
                              @RequestParam(name = "renew-email") String renewEmail,
                              @RequestParam String salt) {
        HttpSession session = request.getSession();
        ForumUser forumUser = authService.getForumUserBySessionId(session.getId());
        if (forumUser == null) return "redirect:/login"; //Если не залогиненный, то кидает на страницу входа

        String sessionId = request.getSession().getId();
        String ip = request.getRemoteAddr();

        Map<String, Object> mapModel = new HashMap<>();
        try{
            authService.changeEmail(forumUser, password, newEmail, renewEmail, sessionId, ip, salt);
            mapModel.put("msg", "Email успешно изменён");
        } catch (RuntimeException e) {
            mapModel.put("msg", e.getMessage());
        }

        String newSalt = saltGenerator.getRandomSalt();
        authService.singForm(sessionId, ip, newSalt);

        String form = "email";
        mapModel.put("salt", newSalt);
        mapModel.put("forum_user", forumUser);
        mapModel.put("form", form);
        Integer totalUnreadCount = 0;
        if (forumUser != null) {
            for (MailMessage mailMessage : forumUser.getReceivedLetters()) {
                if (!mailMessage.getIsRead()) totalUnreadCount++;
            }
        }
        mapModel.put("totalUnreadCount", totalUnreadCount);
        model.addAllAttributes(mapModel);

        return "profile";
    }

    @PostMapping("/user/avatar")
    public String setAvatar(@RequestParam() MultipartFile img, HttpServletRequest request) throws SQLException, IOException {
        HttpSession session = request.getSession();
        ForumUser forumUser = authService.getForumUserBySessionId(session.getId());
        if (forumUser == null) return "redirect:/login"; //Если не залогиненный, то кидает на страницу входа

        Blob blob = new SerialBlob(img.getBytes());
        forumUser.setAvatar(blob);
        forumUser.setDefaultAvatar(false);
        forumUserDao.save(forumUser);

        return "redirect:/profile";
    }

    @Transactional
    @GetMapping(value = "/user/avatar/{userId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getUserAvatar(@PathVariable Long userId) throws SQLException {
        ForumUser forumUser = forumUserDao.findById(userId).orElse(null);
        if (forumUser == null) throw new RuntimeException();

        byte[] avatar = forumUser.getAvatar().getBytes(1, ((int) forumUser.getAvatar().length()));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<byte[]>(avatar, httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/user/disavatar")
    public String disableAvatar(HttpServletRequest request) throws IOException {
        HttpSession session = request.getSession();
        ForumUser forumUser = authService.getForumUserBySessionId(session.getId());
        if (forumUser == null) return "redirect:/";

        String imageUrl = request.getScheme() + "://" + request.getServerName() + "/avatar/default_" + forumUser.getGender().toString().toLowerCase() + ".jpg";
        byte[] bytes = getDefaultAvatarFile(imageUrl);
        Blob blob = null;
        try {
            blob = new SerialBlob(bytes);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        forumUser.setAvatar(blob);
        forumUser.setDefaultAvatar(true);
        forumUserDao.save(forumUser);

        return "redirect:/profile";
    }

    private byte[] getDefaultAvatarFile(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        InputStream is = url.openStream();
        byte[] result = new byte[is.available()];
        is.read(result);
        return result;
    }

    @GetMapping("/user/{id}")
    public String getUserInfo(@PathVariable Long id, HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        ForumUser forumUser = authService.getForumUserBySessionId(session.getId());

        ForumUser targetUser = forumUserDao.findById(id).orElse(null);
        if (targetUser == null) throw new UserNotFoundException("User not found");

        Map<String, Object> mapModel = new HashMap<>();
        mapModel.put("forum_user", forumUser);
        mapModel.put("target_user", targetUser);

        Integer totalUnreadCount = 0;
        if (forumUser != null) {
            for (MailMessage mailMessage : forumUser.getReceivedLetters()) {
                if (!mailMessage.getIsRead()) totalUnreadCount++;
            }
        }
        mapModel.put("totalUnreadCount", totalUnreadCount);

        model.addAllAttributes(mapModel);

        return "userinfo";
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "user not found")
    private class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }
}
