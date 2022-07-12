package com.swp.backend.api.v1.owner.voucher;

import com.swp.backend.model.VoucherModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoucherResponse {
    private int page;
    private int maxResult;
    private String message;
    private List<VoucherModel> vouchers;
}
