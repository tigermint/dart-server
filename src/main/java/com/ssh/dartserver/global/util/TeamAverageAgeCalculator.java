package com.ssh.dartserver.global.util;

import com.ssh.dartserver.domain.team.infra.SingleTeamFriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamAverageAgeCalculator {
    private final SingleTeamFriendRepository singleTeamFriendRepository;

    public Double getAverageAge(List<Integer> userBirthYears) {
        return userBirthYears.stream()
                .map(this::getAge)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }

    private int getAge(int value) {
        return DateTimeUtil.nowFromZone().getYear() - value + 1;
    }
}
