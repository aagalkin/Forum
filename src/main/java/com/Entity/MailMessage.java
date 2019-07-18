package com.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "mail_messages")
public class MailMessage implements Serializable, Comparable<MailMessage> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private ForumUser sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private ForumUser recipient;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    private Date date;

    @Column(name = "theme")
    private String theme;

    @Column(name = "text")
    private String text;

    @Column(name = "isread")
    private Boolean isRead;

    @Override
    public int compareTo(MailMessage o) {
        return o.date.compareTo(this.date);
    }
}
