package com.swp.backend.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateHelper {
    public final static String VIETNAM_ZONE = "Asia/Ho_Chi_Minh";
    public static Timestamp getTimestampAtZone(String zone){
        ZoneId zoneId = ZoneId.of(zone);
        LocalDateTime localDateTime = LocalDateTime.now(zoneId);
        return Timestamp.valueOf(localDateTime);
    }

    public static Timestamp plusMinutes(Timestamp timestamp, long plusTime){
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        localDateTime.plusMinutes(plusTime);
        return Timestamp.valueOf(localDateTime);
    }

}
