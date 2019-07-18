package com.Factories;

import com.Entity.Board;
import org.springframework.stereotype.Component;

import java.util.TreeSet;

@Component
public class BoardFactory {

    public Board createNewSubsector(String name, String desc) {
        Board board = new Board();
        board.setName(name);
        board.setDesc(desc);
        board.setTopics(new TreeSet<>());
        return board;
    }
}
