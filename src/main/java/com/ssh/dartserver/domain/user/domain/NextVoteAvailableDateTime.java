package com.ssh.dartserver.domain.user.domain;

import com.ssh.dartserver.global.util.DateTimeUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
        return new NextVoteAvailableDateTime(DateTimeUtil.nowFromZone());
    }

    public static NextVoteAvailableDateTime plusMinutes(int value) {
        return new NextVoteAvailableDateTime(
                DateTimeUtil.nowFromZone()
                        .plusMinutes(value)
                        .truncatedTo(ChronoUnit.SECONDS));
    }
}
