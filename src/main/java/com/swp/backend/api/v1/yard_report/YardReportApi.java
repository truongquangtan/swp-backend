package com.swp.backend.api.v1.yard_report;

import com.google.gson.Gson;
import com.swp.backend.model.MessageResponse;
import com.swp.backend.model.YardReportModel;
import com.swp.backend.service.SecurityContextService;
import com.swp.backend.service.YardReportService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1")
public class YardReportApi {
    private SecurityContextService securityContextService;
    private YardReportService yardReportService;
    private Gson gson;
    private static final int ITEMS_PER_PAGE_DEFAULT = 5;
    private static final int PAGE_DEFAULT = 1;


    @PostMapping(value = "me/report/yards/{yardId}")
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
    public ResponseEntity<String> getReport(@RequestBody(required = false) GetYardReportForAdminRequest request)
    {
        int itemsPerPage = ITEMS_PER_PAGE_DEFAULT;
        int page = PAGE_DEFAULT;
        if (request != null) {
            page = request.getPage() > 0 ? request.getPage() : page;
            itemsPerPage = request.getItemsPerPage() > 0 ? request.getItemsPerPage() : itemsPerPage;
        }

        List<YardReportModel> yardReportModels = yardReportService.getYardReportsDetail(page, itemsPerPage);
        int maxResult = yardReportService.getNumberOfYardReports();
        GetYardReportForAdminResponse response = new GetYardReportForAdminResponse("Get reports success fully", page, maxResult, yardReportModels);
        return ResponseEntity.ok().body(gson.toJson(response));
    }

    @PutMapping(value = "admin/reports/{reportId}/handle")
    public ResponseEntity<String> markAsResolvedReportInformation(@PathVariable String reportId)
    {
        try
        {
            yardReportService.maskAsResolvedReport(reportId);
            MessageResponse response = new MessageResponse("Masked as resolve successfully.");
            return ResponseEntity.ok().body(gson.toJson(response));
        } catch (Exception ex)
        {
            return ResponseEntity.internalServerError().body("Error when process masked as resolve.");
        }
    }
    @PutMapping(value = "admin/reports/{reportId}/reject")
    public ResponseEntity<String> rejectReportInformation(@PathVariable String reportId)
    {
        try
        {
            yardReportService.rejectReport(reportId);
            MessageResponse response = new MessageResponse("Reject successfully.");
            return ResponseEntity.ok().body(gson.toJson(response));
        } catch (Exception ex)
        {
            return ResponseEntity.internalServerError().body("Error when process reject report.");
        }
    }
}
