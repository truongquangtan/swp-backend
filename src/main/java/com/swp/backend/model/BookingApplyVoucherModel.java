package com.swp.backend.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingApplyVoucherModel extends BookingModel {
    private int discountPrice;

    @Builder
    public BookingApplyVoucherModel(int slotId, String refSubYard, int price, String date, int discountPrice, int originalPrice) {
        super(slotId, refSubYard, price, originalPrice, date);
        this.discountPrice = discountPrice;
    }
}
