package com.swp.backend.api.v1.booking;

import com.swp.backend.model.BookingModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
    private List<BookingModel> bookingList;
    private String voucherId;

    public boolean isValid() {
        for (BookingModel bookingModel : bookingList) {
            if (bookingModel.isValid() == false) {
                return false;
            }
        }
        return true;
    }
}
