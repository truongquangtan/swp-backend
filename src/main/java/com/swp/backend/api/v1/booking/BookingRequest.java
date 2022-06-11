package com.swp.backend.api.v1.booking;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingRequest {
    private int slotId;
}
