package com.ssh.dartserver.domain.vote.domain;

import com.ssh.dartserver.domain.user.domain.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Candidate {
    @Id @GeneratedValue
    @Column(name = "candidate_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Vote vote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Builder
    public Candidate(Vote vote, User user) {
        this.vote = vote;
        this.user = user;
    }

    public void updateUser(Object o) {
        this.user = (User) o;
    }
}
