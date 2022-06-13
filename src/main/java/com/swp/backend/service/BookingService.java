package com.swp.backend.service;

import com.swp.backend.constance.BookingStatus;
import com.swp.backend.entity.BookingEntity;
import com.swp.backend.model.BookingModel;
import com.swp.backend.repository.BookingRepository;
import com.swp.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@AllArgsConstructor
public class BookingService {
    private SlotService slotService;
    private YardService yardService;
    private SubYardService subYardService;
    private BookingRepository bookingRepository;

    public BookingEntity book(String userId, BookingModel bookingModel) {
        String errorNote = "";
        int slotId = bookingModel.getSlotId();

        //SubYard filter
        String subYardId = bookingModel.getRefSubYard();
        if (subYardId == null) {
            errorNote = "Cannot find sub yard from slot id " + slotId;
            return processBooking(userId, bookingModel, errorNote, BookingStatus.FAILED);
        }
        if (!subYardService.isActiveSubYard(subYardId)) {
            errorNote = "SubYard of slot id " + bookingModel.getSlotId() + " is not active";
            return processBooking(userId, bookingModel, errorNote, BookingStatus.FAILED);
        }

        //Slot Not Available Filter
        Timestamp timestamp = DateHelper.parseFromStringToTimestamp(bookingModel.getDate());
        if (!slotService.isSlotActive(slotId)) {
            errorNote = "Slot of slot id " + slotId + " is not active.";
            return processBooking(userId, bookingModel, errorNote, BookingStatus.FAILED);
        }
        if (!slotService.isSlotAvailableFromBooking(slotId, timestamp)) {
            errorNote = "Slot of slot id " + slotId + " is booked.";
            return processBooking(userId, bookingModel, errorNote, BookingStatus.FAILED);
        }

        //Local Time not exceed Start Time
        Timestamp dateRequest = DateHelper.parseFromStringToTimestamp(bookingModel.getDate());
        if (DateHelper.isToday(dateRequest) && !slotService.isSlotExceedTimeToday(slotId)) {
            errorNote = "Slot of slot id " + slotId + " is started";
            return processBooking(userId, bookingModel, errorNote, BookingStatus.FAILED);
        }

        return processBooking(userId, bookingModel, errorNote, BookingStatus.SUCCESS);
    }

    private BookingEntity processBooking(String userId, BookingModel bookingModel, String errorNote, String status) {
        BookingEntity bookingEntity = BookingEntity.builder()
                .accountId(userId)
                .slotId(bookingModel.getSlotId())
                .date(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE))
                .status(status)
                .note(errorNote)
                .price(bookingModel.getPrice())
                .build();

        bookingRepository.save(bookingEntity);

        return bookingEntity;
    }
}
