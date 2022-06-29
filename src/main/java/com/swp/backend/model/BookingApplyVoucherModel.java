package com.swp.backend.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingApplyVoucherModel extends BookingModel {
    private float newPrice;
    private float discountPrice;

    @Builder
    public BookingApplyVoucherModel(int slotId, String refSubYard, int price, String date, float newPrice, float discountPrice) {
        super(slotId, refSubYard, price, date);
        this.newPrice = newPrice;
        this.discountPrice = discountPrice;
    }
}
