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
    private String bigYardName;
    private String subYardName;
    private String type;
    private int price;
    private String address;
    private String time;

    public static BookingHistoryModel buildFromBookingHistoryEntityAndCreatedByAndMatchModel(BookingHistoryEntity bookingHistory, String createdBy, MatchModel matchModel)
    {
        return BookingHistoryModel.builder()
                .bookingId(bookingHistory.getBookingId())
                .bookingStatus(bookingHistory.getBookingStatus())
                .createdAt(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(bookingHistory.getCreatedAt()))
                .createdBy(createdBy)
                .note(bookingHistory.getNote())
                .reference(bookingHistory.getReference())
                .time(matchModel.getStartTime() + " - " + matchModel.getEndTime())
                .bigYardName(matchModel.getBigYardName())
                .subYardName(matchModel.getSubYardName())
                .type(matchModel.getType())
                .price(matchModel.getPrice())
                .address(matchModel.getBigYardAddress())
                .build();
    }
}
