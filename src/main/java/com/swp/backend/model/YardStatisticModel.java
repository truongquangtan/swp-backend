package com.swp.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class YardStatisticModel {
    private String yardId;
    private String ownerId;
    private String yardName;
    private long numberOfBooking;
    private long numberOfBookingCanceled;
    private long numberOfBookingPlayed;
    @Builder.Default
    private int businessContributionPercentage = 0;
    @Builder.Default
    private long totalIncome = 0;
}
