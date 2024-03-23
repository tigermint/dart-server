package com.ssh.dartserver.domain.vote.domain;

import com.ssh.dartserver.domain.question.domain.Question;
import com.ssh.dartserver.domain.user.domain.User;
import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Vote{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id")
    private Long id;

    @Column(name = "picked_time")
    private LocalDateTime pickedTime;

    @Embedded
    private Candidates candidates;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "picking_user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User pickingUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "picked_user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User pickedUser;


    @Builder
    public Vote(List<User> candidates,LocalDateTime pickedTime, Question question, User pickingUser, User pickedUser) {
        this.candidates = Candidates.of(
                candidates.stream()
                      .map(user -> Candidate.builder()
                              .vote(this)
                              .user(user)
                              .build()
                      )
                      .collect(Collectors.toList())
        );
        this.pickedTime = pickedTime;
        this.question = question;
        this.pickingUser = pickingUser;
        this.pickedUser = pickedUser;
    }

    public void updatePickingUser(Object o) {
        this.pickingUser = (User) o;
    }
}
