package com.ssh.dartserver.domain.team.domain.vo;

import java.util.Arrays;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.ToString;

@Embeddable
@Getter
@ToString
@NoArgsConstructor
public class TeamUsersCombinationHash {
    private static final String SEPARATOR = "-";

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

    public List<Long> getUsersId() {
        return Arrays.stream(value.split(SEPARATOR))
                .map(Long::parseLong)
                .toList();
    }
}
