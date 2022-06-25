package com.swp.backend.api.v1.voucher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherRequest {
    private String id;
    private String type;
    private String title;
    private String description;
    private Boolean delete;
    private Integer maxQuantity;
    private Integer remainder;
    private Integer percentDiscount;
    private Integer percentDiscountUpto;
    private Integer amountLeast;
    private Integer amountDiscount;
    private String startDate;
    private String endDate;
    private String yardId;
}
