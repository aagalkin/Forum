package com.dao;

import com.Entity.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectorDao extends JpaRepository<Sector, Long> {

}
