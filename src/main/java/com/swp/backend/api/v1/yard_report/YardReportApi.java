package com.swp.backend.api.v1.yard_report;

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


    @PostMapping(value = "yards/{yardId}/report")
    public ResponseEntity<String> report(@PathVariable String yardId, @RequestBody String reason)
    {
        String userId = securityContextService.extractUsernameFromContext(SecurityContextHolder.getContext());

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
