package com.ssh.dartserver.friend.domain;

import com.ssh.dartserver.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friend {
    @Column(name = "friend_id")
    @Id @GeneratedValue
    private Long id;

    private Long friendUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Builder
    public Friend(Long friendUserId, User user) {
        this.friendUserId = friendUserId;
        this.user = user;
    }
}
