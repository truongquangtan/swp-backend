package com.swp.backend.api.v1.booking_history.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class BookingHistoryRequest {
    private int itemsPerPage;
    private int page;
}
