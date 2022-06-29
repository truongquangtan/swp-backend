package com.swp.backend.api.v1.dashboard.owner;

import com.google.gson.Gson;
import com.swp.backend.model.YardStatisticModel;
import com.swp.backend.myrepository.DashboardRepository;
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
    private DashboardRepository dashboardRepository;
    private SecurityContextService securityContextService;

    @PostMapping("dashboard")
    public ResponseEntity<String> getDashboardStatistic(@RequestBody(required = false) DashboardRequest request)
    {
        List<YardStatisticModel> result;
        Timestamp startDate = Timestamp.valueOf(request.getStartTime() + " 00:00:00");
        Timestamp endDate = Timestamp.valueOf(request.getEndTime() + " 00:00:00");

        SecurityContext context = SecurityContextHolder.getContext();
        String ownerId = securityContextService.extractUsernameFromContext(context);

        result = dashboardRepository.getYardBookingTotalIncomeForOwner(ownerId, startDate, endDate);
        return ResponseEntity.ok().body(gson.toJson(result));
    }
}
