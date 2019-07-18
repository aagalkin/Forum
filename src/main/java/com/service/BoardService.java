package com.service;

import com.Entity.Board;
import com.Entity.Sector;
import com.Entity.Topic;
import com.Factories.BoardFactory;
import com.dao.ForumMessageDao;
import com.dao.SectorDao;
import com.dao.BoardDao;
import com.dao.TopicDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Set;

@Service
public class BoardService {

    @Autowired
    private BoardDao boardDao;

    @Autowired
    private BoardFactory boardFactory;

    @Autowired
    private SectorDao sectorDao;

    @Autowired
    private ForumMessageDao forumMessageDao;

    @Autowired
    private TopicDao topicDao;

    @Transactional
    public void create(String name, String desc, Long sectorId) {
        Board board = boardFactory.createNewSubsector(name, desc);
        Sector masterSector = sectorDao.findById(sectorId).orElse(null);
        if (masterSector == null) throw new RuntimeException("Unknown error");
        board.setSector(masterSector);
        boardDao.save(board);
    }

    @Transactional
    public void delete(Long id) {
        Board board = boardDao.findById(id).orElse(null);
        if (board == null) return;
        Set<Topic> topics = board.getTopics();
        for (Topic topic : topics) {
            forumMessageDao.deleteAll(topic.getForumMessages());
        }
        topicDao.deleteAll(topics);
        boardDao.delete(board);
    }
}
