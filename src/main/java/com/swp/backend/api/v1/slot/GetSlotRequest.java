package com.swp.backend.api.v1.slot;

import com.swp.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

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
