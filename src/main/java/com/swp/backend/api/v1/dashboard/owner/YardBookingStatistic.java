package com.swp.backend.api.v1.dashboard.owner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class YardBookingStatistic {
    private String yardId;
    private String yardName;
    private int numberOfBooking;
    private int numberOfBookingCanceled;
}
