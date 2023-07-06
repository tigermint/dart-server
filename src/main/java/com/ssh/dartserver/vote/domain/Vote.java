package com.ssh.dartserver.vote.domain;

import com.ssh.dartserver.question.domain.Question;
import com.ssh.dartserver.user.domain.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Vote{
    @Id @GeneratedValue
    @Column(name = "vote_id")
    private Long id;

    //list로 관리 but 순서 필요 index로 관리
    private Long firstUserId;
    private Long secondUserId;
    private Long thirdUserId;
    private Long fourthUserId;
    private LocalDateTime pickedTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "picked_user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User pickedUser;

}
