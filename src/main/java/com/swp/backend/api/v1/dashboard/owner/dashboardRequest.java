package com.swp.backend.api.v1.dashboard.owner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class dashboardRequest {
    private String startTime;
    private String endTime;
}
