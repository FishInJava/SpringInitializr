package com.happyzombie.springinitializr.common.util;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Java8的时间转为时间戳的大概的思路就是LocalDateTime先转为Instant，设置时区，然后转timestamp
 * LocalDate
 * LocalDateTime
 * DateTimeFormatter
 *
 * @author admin
 */
public class DateUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter FORMATTER_DAY = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final ZoneOffset CHINA_ZONE_OFFSET = ZoneOffset.of("+8");
    private static final ZoneId CHINA_ZONE_ID = ZoneId.of("CTT", ZoneId.SHORT_IDS);

    public static Long dataStrToMilli(String dataStr) {
        // 格式是yyyy-MM-dd
        return LocalDate.parse(dataStr, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay().toInstant(CHINA_ZONE_OFFSET).toEpochMilli();
    }

    public static Long getCurrentTimestampMilli() {
        final Instant ctt = Clock.system(CHINA_ZONE_ID).instant();
        return ctt.toEpochMilli();
    }

    public static Long dateTimeStrToMilli(String dataStr) {
        // 格式是yyyy-MM-dd HH:mm:ss
        return LocalDateTime.parse(dataStr, FORMATTER).toInstant(CHINA_ZONE_OFFSET).toEpochMilli();
    }

    public static Long dateStrToMilli(String dataStr) {
        // 格式是yyyy-MM-dd
        return LocalDate.parse(dataStr, FORMATTER_DAY).atStartOfDay().toInstant(CHINA_ZONE_OFFSET).toEpochMilli();
    }
    
    public static LocalDate now() {
        return LocalDate.now(CHINA_ZONE_ID);
    }

    /**
     * 今天的milli值
     */
    public static Long dayMilli() {
        return DateUtil.dateStrToMilli(DateUtil.now().toString());
    }

    /**
     * 1649411429567
     */
    public static String timestampMilliToString(Long time) {
        AssertUtil.shouldBeTrue(time != null, "time is null！");
        return FORMATTER.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), CHINA_ZONE_ID));
    }
}
