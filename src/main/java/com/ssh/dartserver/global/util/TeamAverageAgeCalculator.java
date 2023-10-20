package com.ssh.dartserver.global.util;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamAverageAgeCalculator {

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
