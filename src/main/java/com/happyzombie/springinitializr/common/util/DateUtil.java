package com.happyzombie.springinitializr.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Java8的时间转为时间戳的大概的思路就是LocalDateTime先转为Instant，设置时区，然后转timestamp
 * LocalDate
 * LocalDateTime
 * DateTimeFormatter
 */
public class DateUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final ZoneOffset ZONE_OFFSET = ZoneOffset.of("+8");

    public static Long dataStrToMilli(String dataStr) {
        // 格式是yyyy-MM-dd
        return LocalDate.parse(dataStr, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay().toInstant(ZONE_OFFSET).toEpochMilli();
    }

    public static Long dataTimeStrToMilli(String dataStr) {
        // 格式是yyyy-MM-dd HH:mm:ss
        return LocalDateTime.parse(dataStr, FORMATTER).toInstant(ZONE_OFFSET).toEpochMilli();
    }


}
