package com.swp.backend.api.v1.voucher;

import com.swp.backend.model.BookingApplyVoucherModel;
import com.swp.backend.model.BookingModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyVoucherRequest {
    private String voucherCode;
    private List<BookingModel> bookingList;
}
