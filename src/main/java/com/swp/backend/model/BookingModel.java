package com.swp.backend.model;

import com.swp.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingModel {
    protected int slotId;
    protected String refSubYard;
    protected int price;
    protected int originalPrice;
    protected String date;

    public boolean isValid() {
        return DateHelper.parseFromStringToTimestamp(date) != null && price > 0 && originalPrice > 0;
    }
}
