package com.Entity;

import com.DTO.BoardDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "boards")
public class Board implements Serializable, Comparable<Board> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "descr")
    private String desc;

    @ManyToOne()
    @JoinColumn(name = "sector_id")
    private Sector sector;

    @OneToMany(mappedBy = "board")
    @OrderBy("updateTime DESC")
    private Set<Topic> topics;

    @Override
    public int compareTo(Board o) {
        return Long.compare(this.id, o.id);
    }

    public Integer getCountOfMessages() {
        Integer result = 0;
        Iterator<Topic> themeIterator = topics.iterator();
        while (themeIterator.hasNext()) {
            result += themeIterator.next().getForumMessages().size();
        }
        return result;
    }

    @Override
    public String toString() {
        return "Board{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public BoardDTO toDTO() {
        return new BoardDTO(this);
    }

    public Topic getLatestTopic() {
        return topics.iterator().hasNext() ? topics.iterator().next() : null;
    }
}
