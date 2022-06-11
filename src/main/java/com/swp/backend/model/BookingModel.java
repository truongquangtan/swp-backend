package com.swp.backend.model;

import com.swp.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingModel {
    private int slotId;
    private String refSubYard;
    private int price;
    private String date;

    public boolean isValid() {
        return DateHelper.parseFromStringToTimestamp(date) != null && price > 0;
    }
}
