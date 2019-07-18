package com.service;

import com.Entity.Board;
import com.Entity.ForumMessage;
import com.Entity.ForumUser;
import com.Entity.Topic;
import com.Factories.ForumMessageFactory;
import com.Factories.TopicFactory;
import com.dao.ForumMessageDao;
import com.dao.ForumUserDao;
import com.dao.BoardDao;
import com.dao.TopicDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class TopicService {

    @Autowired
    private TopicFactory topicFactory;

    @Autowired
    private ForumMessageFactory forumMessageFactory;

    @Autowired
    private TopicDao topicDao;

    @Autowired
    private ForumUserDao forumUserDao;

    @Autowired
    private BoardDao boardDao;

    @Autowired
    private ForumMessageDao forumMessageDao;

    @Transactional
    public Long create(String name, String desc, String text, Long authorId, Long boardId) {
        ForumUser author = forumUserDao.findById(authorId).orElse(null);
        if (author == null) return null;
        Board board = boardDao.findById(boardId).orElse(null);
        if (board == null) return null;

        Topic topic = topicFactory.createNewTopic(name, desc, author, board);
        topicDao.save(topic);
        ForumMessage forumMessage = forumMessageFactory.createNewMessage(text, author, topic);
        forumMessageDao.save(forumMessage);
        return topic.getId();
    }
}
