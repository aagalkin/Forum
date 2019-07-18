package com.dao;

import com.Entity.ForumUser;
import com.sun.org.apache.xpath.internal.FoundIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForumUserDao extends JpaRepository<ForumUser, Long> {
    Optional<ForumUser> findByNickname(String nickname);

    Optional<ForumUser> findByEmail(String email);
}
