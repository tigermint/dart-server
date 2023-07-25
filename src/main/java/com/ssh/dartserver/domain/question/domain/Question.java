package com.ssh.dartserver.domain.question.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question {
    @Id @GeneratedValue
    @Column(name = "question_id")
    private Long id;

    private String content;

    private String icon;

    @Builder
    public Question(String content, String icon) {
        this.content = content;
        this.icon = icon;
    }
}
