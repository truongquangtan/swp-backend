package com.swp.backend.api.v1.voucher;

import com.swp.backend.entity.VoucherEntity;
import com.swp.backend.model.BookingApplyVoucherModel;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ApplyVoucherResponse {
    private String voucherId;
    private String voucherCode;
    private List<BookingApplyVoucherModel> bookingList;
}
