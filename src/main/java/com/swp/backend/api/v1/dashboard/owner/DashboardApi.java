package com.swp.backend.api.v1.dashboard.owner;

import com.google.gson.Gson;
import com.swp.backend.model.YardStatisticModel;
import com.swp.backend.myrepository.DashboardRepository;
import com.swp.backend.service.DashboardService;
import com.swp.backend.service.SecurityContextService;
import com.swp.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "api/v1/owners/me")
@AllArgsConstructor
public class DashboardApi {
    private Gson gson;
    private DashboardService dashboardService;
    private SecurityContextService securityContextService;
    public static final int ADD_IN_MAX_BOOKING_FOR_BETTER_DISPLAY = 15;

    @PostMapping("dashboard")
    public ResponseEntity<String> getDashboardStatistic(@RequestBody(required = false) DashboardRequest request)
    {
        DashboardResponse response;
        Timestamp startDate = Timestamp.valueOf(request.getStartTime() + " 00:00:00");
        Timestamp endDate = Timestamp.valueOf(request.getEndTime() + " 00:00:00");

        SecurityContext context = SecurityContextHolder.getContext();
        String ownerId = securityContextService.extractUsernameFromContext(context);

        var yardStatistic = dashboardService.processGetAllInformationOfYardStatisticModel(ownerId, startDate, endDate);
        var numberOfBookingsByTime = dashboardService.getBookingByTimeStatistic(ownerId, startDate, endDate).values();
        long maxOfBookings = 0;
        for(YardStatisticModel yardStatisticModel : yardStatistic)
            maxOfBookings = maxOfBookings > yardStatisticModel.getNumberOfBookings() ? maxOfBookings : yardStatisticModel.getNumberOfBookings();
        response = DashboardResponse.builder().message("Get statistic successfully")
                .yardStatistic(yardStatistic)
                .bookingStatisticByTime(numberOfBookingsByTime)
                .maxOfBooking(maxOfBookings + ADD_IN_MAX_BOOKING_FOR_BETTER_DISPLAY)
                .build();
        return ResponseEntity.ok().body(gson.toJson(response));
    }
}
