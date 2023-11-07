package com.ssh.dartserver.domain.survey.domain;

import com.ssh.dartserver.domain.user.domain.User;

import javax.persistence.*;
@Entity
public class CommentReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_report_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
