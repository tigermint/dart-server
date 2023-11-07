package com.ssh.dartserver.domain.survey.domain;

import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.global.common.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class AnswerUser extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_user_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Answer answer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    public void updateAnswer(Answer answer) {
        this.answer = answer;
    }
}
