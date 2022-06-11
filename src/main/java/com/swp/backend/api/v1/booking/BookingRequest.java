package com.swp.backend.api.v1.booking;

import com.swp.backend.model.BookingModel;
import com.swp.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class BookingRequest {
    private List<BookingModel> bookingModels;
    public boolean isValid()
    {
        for(int i = 0; i < bookingModels.size(); ++i)
        {
            BookingModel bookingModel = bookingModels.get(i);
            String date = bookingModel.getDate();
            if(DateHelper.parseFromStringToTimestampOfDate(date) == null)
            {
                return false;
            }
        }
        return true;
    }
}
