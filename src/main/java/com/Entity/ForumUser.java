package com.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Blob;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "forum_user")
public class ForumUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "signature")
    private String signature;

    @Column(name = "reg_date")
    @Temporal(TemporalType.DATE)
    private Date regDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Temporal(TemporalType.DATE)
    @Column(name = "dob")
    private Date dateOfBirth;

    @Column(name = "message_counter")
    private Integer messageCounter;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "avatar")
    @Lob
    private Blob avatar;

    @Column(name = "default_avatar")
    private Boolean defaultAvatar;

    @OneToMany(mappedBy = "author")
    @OrderBy("id")
    private Set<ForumMessage> forumMessages;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @OneToMany(mappedBy = "sender")
    private List<MailMessage> sentLetters;

    @OneToMany(mappedBy = "recipient")
    private List<MailMessage> receivedLetters;

    public ForumUser() {

    }

    @Override
    public String toString() {
        return "ForumUser{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", regDate=" + regDate +
                ", gender=" + gender +
                ", dateOfBirth=" + dateOfBirth +
                ", messageCounter=" + messageCounter +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForumUser forumUser = (ForumUser) o;
        return id.equals(forumUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
