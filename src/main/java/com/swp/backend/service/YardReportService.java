package com.swp.backend.service;

import com.swp.backend.entity.YardReportEntity;
import com.swp.backend.model.YardReportModel;
import com.swp.backend.repository.YardReportRepository;
import com.swp.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class YardReportService {
    public static final String REPORT_NOT_HANDLED = "NOT HANDLED YET";
    public static final String REPORT_HANDLED = "HANDLED";
    private YardReportRepository yardReportRepository;

    public void reportYard(String userId, String yardId, String reason)
    {
        YardReportEntity yardReportEntity = YardReportEntity.builder().yardId(yardId)
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .reason(reason)
                .createdAt(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE))
                .updatedAt(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE))
                .build();
        yardReportRepository.save(yardReportEntity);
    }

    public List<YardReportModel> getYardReportDetail(String reportId)
    {
        return new ArrayList<>();
    }
}
