package com.swp.backend.api.v1.booking;

import com.swp.backend.entity.BookingEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class BookingResponse {
    private String message;
    private boolean isError;
    private List<BookingEntity> bookings;
}
