package com.swp.backend.model;

import com.swp.backend.entity.BookingHistoryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookingHistoryModel {
    private long reference;
    private String bookingId;
    private String createdAt;
    private String createdBy;
    private String note;
    private String bookingStatus;

    public static BookingHistoryModel buildFromBookingHistoryEntityAndCreatedBy(BookingHistoryEntity bookingHistory, String createdBy)
    {
        return BookingHistoryModel.builder()
                .bookingId(bookingHistory.getBookingId())
                .bookingStatus(bookingHistory.getBookingStatus())
                .createdAt(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(bookingHistory.getCreatedAt()))
                .createdBy(createdBy)
                .note(bookingHistory.getNote())
                .reference(bookingHistory.getBookingReference())
                .build();
    }
}
