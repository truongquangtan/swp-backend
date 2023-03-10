package com.swp.backend.api.v1.booking_history;

import com.swp.backend.model.BookingHistoryModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BookingHistoryResponse {
    private String message;
    private int page;
    private int maxResult;
    private List<BookingHistoryModel> data;
}
