package com.swp.backend.model;

import com.swp.backend.entity.BookingEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingDTO {
    private int id;
    private String accountId;
    private int slotId;
    private String status;
    private Timestamp date;
    private String note;
    private int price;
    private Timestamp bookAt;

    public static BookingDTO getFromBookingEntity(BookingEntity bookingEntity)
    {
        return new BookingDTO(bookingEntity.getId(), bookingEntity.getAccountId(), bookingEntity.getSlotId(),
                                bookingEntity.getStatus(), bookingEntity.getDate(), bookingEntity.getNote(),
                                bookingEntity.getPrice(), bookingEntity.getBookAt());
    }
}
