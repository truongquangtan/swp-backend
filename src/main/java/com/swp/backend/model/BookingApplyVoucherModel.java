package com.swp.backend.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingApplyVoucherModel extends BookingModel{
    private int newPrice;
    private int discountPrice;

    @Builder
    public BookingApplyVoucherModel(int slotId, String refSubYard, int price, String date,  int newPrice, int discountPrice) {
        super(slotId, refSubYard, price, date);
        this.newPrice = newPrice;
        this.discountPrice = discountPrice;
    }
}
