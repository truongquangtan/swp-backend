package com.swp.backend.api.v1.book.cancel_booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CancelBookingRequest {
    private String reason;
    private String subYardId;
    private String yardId;
    private int slotId;

    public boolean isValid() {
        return reason != null && subYardId != null && yardId != null
                && !reason.trim().isEmpty() && !subYardId.trim().isEmpty() && !yardId.trim().isEmpty();
    }
}
