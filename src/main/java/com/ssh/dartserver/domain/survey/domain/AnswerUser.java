package com.ssh.dartserver.domain.survey.domain;

import com.ssh.dartserver.domain.user.domain.User;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class AnswerUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_user_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Answer answer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;
}
