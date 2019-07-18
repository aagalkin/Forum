package com.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "forum_message")
public class ForumMessage implements Serializable, Comparable<ForumMessage> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "theme_id")
    private Topic topic;

    @ManyToOne()
    @JoinColumn(name = "author_id")
    private ForumUser author;

    @Column(name = "text")
    private String text;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    private Date createTime;

    @Override
    public int compareTo(ForumMessage o) {
        return Long.compare(this.id, o.id);
    }
}
