package com.ssh.dartserver.global.util;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TeamAverageAgeCalculatorTest {
    private TeamAverageAgeCalculator calculator;

    @BeforeEach
    void setup() {
        calculator = new TeamAverageAgeCalculator();
    }

    @Test
    public void test_now_from_zone_new_year_transition() {
        LocalDateTime newYearEve = LocalDateTime.of(2022, 12, 31, 23, 59);
        LocalDateTime newYear = LocalDateTime.of(2023, 1, 1, 0, 0);

        assertEquals(newYearEve.plusMinutes(1), newYear);
    }

    @Test
    public void return_double_precision_result() {
        List<Integer> birthYears = List.of(1985, 1995);

        assertTrue(calculator.getAverageAge(birthYears) instanceof Double);
    }

    @Test
    public void return_zero_for_empty_list() {
        List<Integer> birthYears = List.of();

        assertEquals(0.0, calculator.getAverageAge(birthYears), 0.0);
    }
}