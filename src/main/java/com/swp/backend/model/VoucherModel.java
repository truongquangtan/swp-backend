package com.swp.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoucherModel {
    private String id;
    private String type;
    private String title;
    private String description;
    private Boolean isActive;
    private String voucherCode;
    private Integer maxQuantity;
    private Integer remainder;
    private Integer percentDiscount;
    private Integer amountDiscount;
    private String startDate;
    private String endDate;
    private String createdByAccountId;
    private String createdAt;
    private String status;
    private int reference;
}
