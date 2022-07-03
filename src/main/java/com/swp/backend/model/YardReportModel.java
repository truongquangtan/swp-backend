package com.swp.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class YardReportModel {
    private String reportId;
    private String userId;
    private String yardId;
    private String ownerId;
    private String userName;
    private String yardName;
    private String yardAddress;
    private String ownerEmail;
    private String status;
    private String createdAt;
    private String updatedAt;
}
