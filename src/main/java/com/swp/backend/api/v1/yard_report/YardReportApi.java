package com.swp.backend.api.v1.yard_report;

import com.google.gson.Gson;
import com.swp.backend.model.MessageResponse;
import com.swp.backend.service.SecurityContextService;
import com.swp.backend.service.YardReportService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1")
public class YardReportApi {
    private SecurityContextService securityContextService;
    private YardReportService yardReportService;
    private Gson gson;


    @PostMapping(value = "yards/{yardId}/report")
    public ResponseEntity<String> report(@PathVariable String yardId, @RequestBody YardReportOfUserRequest request)
    {
        try
        {
            String userId = securityContextService.extractUsernameFromContext(SecurityContextHolder.getContext());
            yardReportService.reportYard(userId, yardId, request.getReason());
            MessageResponse response = new MessageResponse("Report successfully");
            return ResponseEntity.ok().body(gson.toJson(response));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            ResponseEntity.internalServerError().body("Error when process report.");
        }

        return ResponseEntity.ok().body("");
    }

    @PostMapping(value = "admin/reports")
    public ResponseEntity<String> getReport()
    {
        return ResponseEntity.ok().body("admin/report-information");
    }

    @PutMapping(value = "admin/reports/{reportId}")
    public ResponseEntity<String> markAsResolvedReportInformation(@PathVariable String reportId)
    {
        return ResponseEntity.ok().body("");
    }
}
