package com.Factories;

import com.Entity.ForumUser;
import com.Entity.Board;
import com.Entity.Topic;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.TreeSet;

@Component
public class TopicFactory {

    public Topic createNewTopic(String name, String desc, ForumUser author, Board board) {
        Topic topic = new Topic();
        topic.setBoard(board);
        topic.setAuthor(author);
        topic.setUpdateTime(new Date());
        topic.setName(name);
        topic.setDesc(desc);
        topic.setForumMessages(new TreeSet<>());
        return topic;
    }
}
