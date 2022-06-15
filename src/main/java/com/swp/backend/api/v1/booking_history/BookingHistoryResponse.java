package com.swp.backend.api.v1.booking_history;

import com.swp.backend.model.MatchModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookingHistoryResponse {
    private String message;
    private int page;
    private int maxResult;
    private List<MatchModel> data;
}
