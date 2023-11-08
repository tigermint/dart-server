package com.ssh.dartserver.domain.survey.domain;

import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Answer {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long id;

    @Embedded
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Survey survey;

    @OneToMany(mappedBy = "answer")
    private List<AnswerUser> answerUsers = new ArrayList<>();

}
