package com.swp.backend.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class DateHelper {
    public final static String VIETNAM_ZONE = "Asia/Ho_Chi_Minh";
    public static Timestamp getTimestampAtZone(String zone){
        ZoneId zoneId = ZoneId.of(zone);
        LocalDateTime localDateTime = LocalDateTime.now(zoneId);
        return Timestamp.valueOf(localDateTime);
    }

    public static Timestamp plusMinutes(Timestamp timestamp, long plusTime){
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        localDateTime =  localDateTime.plusMinutes(plusTime);
        return Timestamp.valueOf(localDateTime);
    }

    public static Timestamp parseFromStringToTimestamp(String input) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        Date dateParsed;
        Timestamp timestamp;
        try {
            dateParsed = format.parse(input);
            timestamp = Timestamp.from(dateParsed.toInstant());
        } catch (Exception ex) {
            return null;
        }
        return timestamp;
    }

    public static Timestamp parseFromStringToTimestampOfDate(String input) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date dateParsed;
        Timestamp timestamp;
        try {
            dateParsed = format.parse(input);
            timestamp = Timestamp.from(dateParsed.toInstant());
        } catch (Exception ex) {
            return null;
        }
        return timestamp;
    }

    public static Date parseFromStringToDate(String input)
    {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

        Date dateParsed;
        try {
            dateParsed = format.parse(input);
        } catch (Exception ex)
        {
            return null;
        }
        return dateParsed;
    }

    public static boolean isToday(Date requestDate)
    {
        Timestamp today = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String todayFormatted = format.format(today);
        String dateFormatted = format.format(requestDate);
        return dateFormatted.equals(todayFormatted);
    }

    public static LocalTime getLocalTimeFromTimeStamp(Timestamp timestamp)
    {
        Date date = new Date(timestamp.getTime());
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String formattedLocalTime = format.format(date);
        LocalTime localTime = LocalTime.parse(formattedLocalTime);
        return localTime;
    }
    public static LocalTime getLocalTimeFromDateString(String date)
    {
        Timestamp timestamp = DateHelper.parseFromStringToTimestamp(date);
        try {
            LocalTime localTime = DateHelper.getLocalTimeFromTimeStamp(timestamp);
            return localTime;
        } catch (Exception ex){
            return null;
        }
    }
}
