package com.dao;

import com.Entity.MailMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailMessageDao extends JpaRepository<MailMessage, Long> {

}
