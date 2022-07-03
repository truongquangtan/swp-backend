package com.swp.backend.service;

import com.swp.backend.entity.YardReportEntity;
import com.swp.backend.model.YardReportModel;
import com.swp.backend.myrepository.YardReportCustomRepository;
import com.swp.backend.repository.YardReportRepository;
import com.swp.backend.utils.DateHelper;
import com.swp.backend.utils.PaginationHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class YardReportService {
    public static final String REPORT_NOT_HANDLED = "NOT HANDLED YET";
    public static final String REPORT_HANDLED = "HANDLED";
    private YardReportRepository yardReportRepository;
    private YardReportCustomRepository yardReportCustomRepository;

    public void reportYard(String userId, String yardId, String reason)
    {
        YardReportEntity yardReportEntity = YardReportEntity.builder().yardId(yardId)
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .reason(reason)
                .createdAt(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE))
                .updatedAt(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE))
                .status(REPORT_NOT_HANDLED)
                .build();
        yardReportRepository.save(yardReportEntity);
    }

    public List<YardReportModel> getYardReportsDetail(int page, int itemsPerPage)
    {
        PaginationHelper paginationHelper = new PaginationHelper(itemsPerPage, yardReportCustomRepository.countAllYardReports());
        return yardReportCustomRepository.getYardReportModelByPage(paginationHelper.getStartIndex(page), paginationHelper.getEndIndex(page));
    }
    public int getNumberOfYardReports()
    {
        return yardReportCustomRepository.countAllYardReports();
    }
    public void maskAsResolvedReport(String reportId)
    {
        YardReportEntity yardReportEntity = yardReportRepository.findYardReportEntityById(reportId);
        yardReportEntity.setStatus(REPORT_HANDLED);
        yardReportEntity.setUpdatedAt(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE));
        yardReportRepository.save(yardReportEntity);
    }
}
