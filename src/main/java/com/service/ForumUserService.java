package com.service;

import com.Entity.ForumUser;
import com.dao.ForumUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ForumUserService {

    @Autowired
    private ForumUserDao forumUserDao;

    public boolean containsForumUserByNickname(String nickname) {
        ForumUser forumUser = forumUserDao.findByNickname(nickname).orElse(null);
        if (forumUser != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean containsForumUserByEmail(String email) {
        ForumUser forumUser = forumUserDao.findByEmail(email).orElse(null);
        if (forumUser != null) {
            return true;
        } else {
            return false;
        }
    }
}
