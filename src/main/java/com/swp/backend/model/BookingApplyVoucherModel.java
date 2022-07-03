package com.swp.backend.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingApplyVoucherModel extends BookingModel {
    private int originalPrice;
    private int discountPrice;

    @Builder
    public BookingApplyVoucherModel(int slotId, String refSubYard, int price, String date, int originalPrice, int discountPrice) {
        super(slotId, refSubYard, price, date);
        this.originalPrice = originalPrice;
        this.discountPrice = discountPrice;
    }
}
