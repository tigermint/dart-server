package com.ssh.dartserver.user.domain;

import com.ssh.dartserver.common.utils.DateTimeUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor
public class NextVoteAvailableDateTime {

    @Column(name = "next_vote_available_date_time")
    private LocalDateTime value;

    public NextVoteAvailableDateTime(LocalDateTime value) {
        this.value = value;
    }

    public static NextVoteAvailableDateTime newInstance() {
        return new NextVoteAvailableDateTime(
                DateTimeUtils.nowFromZone()
        );
    }

    public static NextVoteAvailableDateTime plusMinutes(int value) {
        return new NextVoteAvailableDateTime(
                DateTimeUtils.nowFromZone().plusMinutes(value)
        );
    }
}
