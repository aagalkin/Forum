package com.DTO;

import com.Entity.Board;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BoardDTO implements Serializable {
    private Long id;
    private String name;
    private Long sectorId;
    private String sectorName;

    public BoardDTO(Board board) {
        this.id = board.getId();
        this.name = board.getName();
        this.sectorId = board.getSector().getId();
        this.sectorName = board.getSector().getName();
    }
}
