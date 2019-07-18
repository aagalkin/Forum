package com.service;

import com.Entity.Sector;
import com.Entity.Board;
import com.Entity.Topic;
import com.Factories.SectorFactory;
import com.dao.ForumMessageDao;
import com.dao.SectorDao;
import com.dao.BoardDao;
import com.dao.TopicDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Set;
import java.util.TreeSet;

@Service
public class SectorService {

    @Autowired
    private SectorDao sectorDao;

    @Autowired
    private SectorFactory sectorFactory;

    @Autowired
    private ForumMessageDao forumMessageDao;

    @Autowired
    private BoardDao boardDao;

    @Autowired
    private TopicDao topicDao;

    public void createSector(String name) {
        Sector sector = sectorFactory.createNewSector(name);
        sectorDao.save(sector);
    }

    @Transactional
    public void delete(Long id) {
        Sector sector = sectorDao.findById(id).orElse(null);
        if (sector == null) return;
        Set<Board> boards = sector.getBoards();
        for (Board board : boards) {
            for (Topic topic : board.getTopics()) {
                forumMessageDao.deleteAll(topic.getForumMessages());
            }
            topicDao.deleteAll(board.getTopics());
        }
        boardDao.deleteAll(boards);
        sectorDao.delete(sector);
    }

    public Set<Sector> getAll() {
        return new TreeSet<>(sectorDao.findAll());
    }
}
