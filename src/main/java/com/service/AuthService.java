package com.service;

import com.Entity.ForumUser;
import com.Entity.Gender;
import com.Factories.ForumUserFactory;
import com.dao.ForumUserDao;
import com.google.common.hash.Hashing;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialBlob;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.*;

@Service
public class AuthService {

    private Map<String, Long> sessions = new HashMap<>();

    private List<AuthToken> tokenList = new ArrayList<>();

    @Autowired
    private TokenCleaner tokenCleaner;

    @Autowired
    private ForumUserDao forumUserDao;

    @Autowired
    private ForumUserService forumUserService;

    @Autowired
    private ForumUserFactory forumUserFactory;

    public ForumUser getForumUserBySessionId(String sessionId) {
        if (!sessions.containsKey(sessionId)) return null;

        Long forumUserId = sessions.get(sessionId);
        return forumUserDao.findById(forumUserId).orElse(null);
    }

    @Transactional
    public void registerNewForumUser(String nickname, Date dayOfBirth, String password, String repassword, String email, Gender gender, String schema, String serverName) throws IOException {
        if (!password.equals(repassword)) throw new RuntimeException("Пароли не совпадают");
        System.out.println("пароли совпадают");
        if (forumUserService.containsForumUserByNickname(nickname)) throw new RuntimeException("Этот псевдоним уже занят");
        System.out.println("ник свободен");
        if (forumUserService.containsForumUserByEmail(email)) throw new RuntimeException("Этот имейл уже используется");
        System.out.println("мыло свободно");

        ForumUser forumUser = forumUserFactory.createNewForumUser(nickname, dayOfBirth, password, email, gender);

        String imageUrl = schema + "://" + serverName + "/avatar/default_" + forumUser.getGender().toString().toLowerCase() + ".jpg";
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
    }

    private byte[] getDefaultAvatarFile(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        InputStream is = url.openStream();
        byte[] result = new byte[is.available()];
        is.read(result);
        return result;
    }

    public boolean isContainsSessionId(String sessionId) {
        return sessions.containsKey(sessionId);
    }

    public void checkToken(String sessionId, String ip, String salt) {
        boolean found = false;
        AuthToken authToken = new AuthToken(sessionId, ip, salt);
        for (AuthToken token : tokenList) {
            if (token.equals(authToken)) {
                if (new Date().after(new Date(token.getDate().getTime() + 60_000))) {
                    tokenList.remove(token);
                    throw new RuntimeException("Форма устарела, попробуйте еще раз");
                }
                found = true;
                break;
            }
        }
        if (!found) throw new RuntimeException("token error!!!");
        tokenList.remove(authToken);
    }

    public void changePassword(ForumUser forumUser, String password, String newPassword, String renewPassword, String sessionId, String ip, String salt) {
        checkToken(sessionId, ip, salt);
        String hashedUserPassword = Hashing.sha256().hashString(forumUser.getPassword() + salt, StandardCharsets.UTF_8).toString();
        if (!hashedUserPassword.equals(password)) {
            throw new RuntimeException("Не верный пароль");
        }

        if (!newPassword.equals(renewPassword)) throw new RuntimeException("Пароли не совпадают");

        forumUser.setPassword(newPassword);
        forumUserDao.save(forumUser);
    }

    public void login(String email, String password, String sessionId, String ip, String salt) {
        checkToken(sessionId, ip, salt);
        ForumUser forumUser = forumUserDao.findByEmail(email).orElse(null);
        if (forumUser == null) {
            throw new RuntimeException("Не верный email или пароль");
        }

        String hashedUserPassword = Hashing.sha256().hashString(forumUser.getPassword() + salt, StandardCharsets.UTF_8).toString();
        if (!hashedUserPassword.equals(password)) {
            throw new RuntimeException("Не верный email или пароль");
        }

        sessions.put(sessionId, forumUser.getId());
    }

    public void logout(String sessionId) {
        sessions.remove(sessionId);
    }

    public void singForm(String sessionId, String ip, String salt) {
        tokenList.add(new AuthToken(sessionId, ip, salt));
    }

    public void changeEmail(ForumUser forumUser, String password, String newEmail, String renewEmail, String sessionId, String ip, String salt) {
        checkToken(sessionId, ip, salt);
        String hashedUserPassword = Hashing.sha256().hashString(forumUser.getPassword() + salt, StandardCharsets.UTF_8).toString();
        if (!hashedUserPassword.equals(password)) {
            throw new RuntimeException("Не верный пароль");
        }

        if (!newEmail.equals(renewEmail)) throw new RuntimeException("Email адреса не совпадают");

        forumUser.setEmail(renewEmail);
        forumUserDao.save(forumUser);
    }

    @Getter
    class AuthToken {
        private String sessionId;
        private String ip;
        private String salt;
        private Date date;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AuthToken authToken = (AuthToken) o;
            return sessionId.equals(authToken.sessionId) &&
                    ip.equals(authToken.ip) &&
                    salt.equals(authToken.salt);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sessionId, ip, salt);
        }

        @Override
        public String toString() {
            return "AuthToken{" +
                    "sessionId='" + sessionId + '\'' +
                    ", ip='" + ip + '\'' +
                    ", salt='" + salt + '\'' +
                    '}';
        }

        public AuthToken(String sessionId, String ip, String salt) {
            this.sessionId = sessionId;
            this.ip = ip;
            this.salt = salt;
            date = new Date();
        }
    }

    class TokenCleaner extends Thread {

        private Date date;
        private Integer min = 60_000;

        @Override
        public void run() {
            while (!isInterrupted()) {
                List<AuthToken> expireTokens = new ArrayList<>();
                date = new Date();
                for (AuthToken authToken : tokenList) {
                    if (date.before(new Date(authToken.getDate().getTime() + min))) {
                        expireTokens.add(authToken);
                    }
                }
                for (AuthToken authToken : expireTokens) {
                    tokenList.remove(authToken);
                }
                expireTokens = null;
                try {
                    Thread.sleep(min * 5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Bean
    public TokenCleaner getTokenCleaner() {
        TokenCleaner tokenCleaner = new TokenCleaner();
        tokenCleaner.setDaemon(true);
        tokenCleaner.start();
        return tokenCleaner;
    }
}
