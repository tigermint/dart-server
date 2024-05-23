package com.ssh.dartserver.global.util;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class DateTimeUtilTest {
    @Test
    public void test_now_from_zone_returns_seoul_time() {
        LocalDateTime expected = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime actual = DateTimeUtil.nowFromZone();

        assertEquals(expected.getHour(), actual.getHour());
        assertEquals(expected.getMinute(), actual.getMinute());
    }

    @Test
    public void test_to_utc_conversion() {
        LocalDateTime kstTime = LocalDateTime.of(2023, 1, 1, 12, 0); // Noon in KST
        LocalDateTime expectedUtcTime = LocalDateTime.of(2023, 1, 1, 3, 0); // UTC is 9 hours behind KST
        LocalDateTime actualUtcTime = DateTimeUtil.toUTC(kstTime);

        assertEquals(expectedUtcTime, actualUtcTime);
    }

    @Test
    public void test_to_utc_at_dst_boundary() {
        LocalDateTime beforeDst = LocalDateTime.of(2023, 3, 12, 1, 59); // Just before DST starts in Seoul
        LocalDateTime afterDst = LocalDateTime.of(2023, 3, 12, 2, 0); // Just after DST starts in Seoul
        LocalDateTime expectedBefore = LocalDateTime.of(2023, 3, 11, 16, 59); // UTC conversion
        LocalDateTime expectedAfter = LocalDateTime.of(2023, 3, 11, 17, 0); // UTC conversion

        assertEquals(expectedBefore, DateTimeUtil.toUTC(beforeDst));
        assertEquals(expectedAfter, DateTimeUtil.toUTC(afterDst));
    }

    @Test
    public void test_now_from_zone_new_year_transition() {
        LocalDateTime newYearEve = LocalDateTime.of(2022, 12, 31, 23, 59);
        LocalDateTime newYear = LocalDateTime.of(2023, 1, 1, 0, 0);

        assertEquals(newYearEve.plusMinutes(1), newYear);
    }
}