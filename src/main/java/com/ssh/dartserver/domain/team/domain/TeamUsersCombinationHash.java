package com.ssh.dartserver.domain.team.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
@Getter
@NoArgsConstructor
public class TeamUsersCombinationHash {
    @Column(name = "team_users_combination_hash", unique = true)
    private String value;

    public TeamUsersCombinationHash(String value) {
        this.value = value;
    }

    public static TeamUsersCombinationHash of(List<Long> values) {
        Collections.sort(values);
        return new TeamUsersCombinationHash(
                values.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining("-"))
        );
    }
}
