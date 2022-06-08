package com.swp.backend.api.v1.slot;

import com.google.type.DateTime;
import com.swp.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;

@Data
@AllArgsConstructor
public class GetSlotRequest {
    private String subYardId;
    private String date;

    public boolean isValid()
    {
        Timestamp dateParsed = DateHelper.parseFromStringToTimestamp(date);
        return dateParsed != null;
    }
}
