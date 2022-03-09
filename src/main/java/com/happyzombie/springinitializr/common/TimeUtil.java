package com.happyzombie.springinitializr.common;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @Author hbz
 * @Date 2021/01/17
 */
public final class TimeUtil {

    private static final ZoneId CITY_SHANG_HAI = ZoneId.of("Asia/Shanghai");

    private static ZonedDateTime todayStart;

    private static ZonedDateTime todayStop;

    private static ZonedDateTime yesterdayStart;

    private static Long todayLongStart;

    private static Long todayLongEnd;

    /**
     * 是否是第二天
     * @return true
     */
    private static boolean isNextDay() {

        if (todayLongStart == null || todayLongEnd == null){
            todayStart = LocalDate.now(CITY_SHANG_HAI).atStartOfDay(CITY_SHANG_HAI);
            todayStop = LocalDate.now(CITY_SHANG_HAI).plusDays(1).atStartOfDay(CITY_SHANG_HAI);
            yesterdayStart = LocalDate.now(CITY_SHANG_HAI).plusDays(-1).atStartOfDay(CITY_SHANG_HAI);
            // 暂存毫秒值
            todayLongStart = todayStart.toInstant().toEpochMilli();
            todayLongEnd = todayStop.toInstant().toEpochMilli();

            return true;
        }

        // 获取当前时间
        long now = Instant.now().toEpochMilli();

        if (now > todayLongStart && now < todayLongEnd){
            return false;
        }else {
            //log.info("过天，更新时间");
            todayStart = LocalDate.now(CITY_SHANG_HAI).atStartOfDay(CITY_SHANG_HAI);
            todayStop = LocalDate.now(CITY_SHANG_HAI).plusDays(1).atStartOfDay(CITY_SHANG_HAI);
            yesterdayStart = LocalDate.now(CITY_SHANG_HAI).plusDays(-1).atStartOfDay(CITY_SHANG_HAI);
            // 暂存毫秒值
            todayLongStart = todayStart.toInstant().toEpochMilli();
            todayLongEnd = todayStop.toInstant().toEpochMilli();
            // todo OPEN_PRICE.clear();
            return true;
        }
    }

}
