package com.ssh.dartserver.user.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor
public class NextVoteAvailableDateTime {

    @Column
    private LocalDateTime value;


    public NextVoteAvailableDateTime(LocalDateTime value) {
        this.value = value;
    }
}
