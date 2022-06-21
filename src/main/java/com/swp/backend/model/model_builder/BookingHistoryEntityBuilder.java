package com.swp.backend.model.model_builder;

import com.swp.backend.entity.BookingEntity;
import com.swp.backend.entity.BookingHistoryEntity;

import java.util.UUID;

public class BookingHistoryEntityBuilder {
    public static BookingHistoryEntity buildFromBookingEntity(BookingEntity bookingEntity, String reason)
    {
        String id = UUID.randomUUID().toString();
        BookingHistoryEntity bookingHistoryEntity = BookingHistoryEntity.builder()
                .bookingId(bookingEntity.getId())
                .createdBy(bookingEntity.getAccountId())
                .createdAt(bookingEntity.getBookAt())
                .bookingStatus(bookingEntity.getStatus())
                .id(id)
                .note(reason)
                .build();
        return bookingHistoryEntity;
    }
}
