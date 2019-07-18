package com.Factories;

import com.Entity.Sector;
import com.Entity.Board;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.TreeSet;

@Component
public class SectorFactory {

    public Sector createNewSector(String name) {
        Sector sector = new Sector();
        sector.setName(name);
        Set<Board> boards = new TreeSet<>();
        sector.setBoards(boards);
        return sector;
    }
}
