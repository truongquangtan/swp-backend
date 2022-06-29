package com.swp.backend.api.v1.dashboard.owner;

import com.swp.backend.model.YardStatisticModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DashboardResponse {
    private String message;
    private List<YardStatisticModel> yardStatistic;
    private Collection bookingStatisticByTime;
    private long maxOfBooking;
}
