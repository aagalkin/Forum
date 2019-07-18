package com.service;

import com.Entity.ForumMessage;
import com.Entity.ForumUser;
import com.Entity.Topic;
import com.Factories.ForumMessageFactory;
import com.dao.ForumMessageDao;
import com.dao.ForumUserDao;
import com.dao.TopicDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ForumMessageService {

    @Autowired
    private ForumMessageFactory forumMessageFactory;

    @Autowired
    private ForumUserDao forumUserDao;

    @Autowired
    private TopicDao topicDao;

    @Autowired
    private ForumMessageDao forumMessageDao;

    public Long create(String text, Long authorId, Long topicId) {
        ForumUser author = forumUserDao.findById(authorId).orElse(null);
        if (author == null) return null;
        Topic topic = topicDao.findById(topicId).orElse(null);
        if (topic == null) return null;
        ForumMessage forumMessage = forumMessageFactory.createNewMessage(text, author, topic);
        topic.setUpdateTime(forumMessage.getCreateTime());
        topicDao.save(topic);
        forumMessageDao.save(forumMessage);
        return forumMessage.getId();
    }
}
