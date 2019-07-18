package com.Factories;

import com.Entity.ForumMessage;
import com.Entity.ForumUser;
import com.Entity.Topic;
import com.tools.Security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ForumMessageFactory {

    @Autowired
    Security security;

    public ForumMessage createNewMessage(String text, ForumUser author, Topic topic) {
        ForumMessage forumMessage = new ForumMessage();
        forumMessage.setCreateTime(new Date());
        forumMessage.setText(security.xssSecurity(text));
        forumMessage.setAuthor(author);
        forumMessage.setTopic(topic);
        return forumMessage;
    }
}
