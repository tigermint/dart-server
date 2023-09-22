package com.ssh.dartserver.global.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateTimeUtil {

    private DateTimeUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static LocalDateTime nowFromZone() {
        return ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();
    }

    public static LocalDateTime toUTC(LocalDateTime kstLocalDateTime) {
        ZonedDateTime kstZonedDateTime = ZonedDateTime.of(kstLocalDateTime, ZoneId.of("Asia/Seoul"));
        ZonedDateTime utcZonedDateTime = kstZonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
        return utcZonedDateTime.toLocalDateTime();
    }
}