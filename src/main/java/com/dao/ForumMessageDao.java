package com.dao;

import com.Entity.ForumMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumMessageDao extends JpaRepository<ForumMessage, Long> {

}
