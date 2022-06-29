package com.swp.backend.api.v1.dashboard.owner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DashboardResponse {
    private List<YardBusinessContribution> yardsBusinessContribution;
    private List<YardBookingStatistic> yardsBookingStatistic;
    private List<Integer> bookingStatisticByTime; //list of number - statistic: From 0h - 24h; Duration: 30m - Size: 48
}
