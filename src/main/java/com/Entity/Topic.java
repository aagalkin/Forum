package com.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "topics")
public class Topic implements Serializable, Comparable<Topic> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "descr")
    private String desc;

    @ManyToOne()
    @JoinColumn(name = "subsector_id")
    private Board board;

    @OneToMany(mappedBy = "topic")
    @OrderBy("id")
    private Set<ForumMessage> forumMessages;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time")
    private Date updateTime;

    @ManyToOne()
    @JoinColumn(name = "author_id")
    private ForumUser author;

    @Override
    public int compareTo(Topic o) {
        return Long.compare(this.getLastMessage().getId(), o.getLastMessage().getId());
    }

    public ForumMessage getLastMessage() {
        List<ForumMessage> forumMessages = new ArrayList<>(getForumMessages());
        return forumMessages.get(forumMessages.size() - 1);
    }
}
