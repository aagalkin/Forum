package com.dao;

import com.Entity.MailBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailBoxDao extends JpaRepository<MailBox, Long> {

}
