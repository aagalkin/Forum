package com.Factories;

import com.Entity.ForumUser;
import com.Entity.Gender;
import com.Entity.MailBox;
import com.Entity.Role;
import com.dao.ForumUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ForumUserFactory {

    @Autowired
    ForumUserDao forumUserDao;

    @Autowired
    MailBoxFactory mailBoxFactory;

    public ForumUser createNewForumUser(String nickname, Date dayOfBirth, String password, String email, Gender gender) {
        ForumUser forumUser = new ForumUser();
        forumUser.setNickname(nickname);
        forumUser.setDateOfBirth(dayOfBirth);
        forumUser.setPassword(password);
        forumUser.setEmail(email);
        forumUser.setGender(gender);
        forumUser.setMessageCounter(0);
        forumUser.setRegDate(new Date());
        forumUser.setSignature("");
        forumUser.setRole(Role.USER);
        return forumUser;
    }
}
