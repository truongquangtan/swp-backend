package com.swp.backend.service;

import com.swp.backend.constance.BookingStatus;
import com.swp.backend.entity.BookingEntity;
import com.swp.backend.model.BookingModel;
import com.swp.backend.myrepository.BookingCustomRepository;
import com.swp.backend.repository.BookingRepository;
import com.swp.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingService {
    private SlotService slotService;
    private SubYardService subYardService;
    private BookingRepository bookingRepository;
    private BookingCustomRepository bookingCustomRepository;

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
        Timestamp timestamp = DateHelper.parseFromStringToTimestamp(bookingModel.getDate());
        Timestamp now = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);

        BookingEntity bookingEntity = BookingEntity.builder()
                .accountId(userId)
                .slotId(bookingModel.getSlotId())
                .date(timestamp)
                .status(status)
                .note(errorNote)
                .price(bookingModel.getPrice())
                .bookAt(now)
                .build();

        bookingRepository.save(bookingEntity);

        return bookingEntity;
    }

    public List<BookingEntity> getIncomingMatchesOfUser(String userId, int itemsPerPage, int page)
    {
        Timestamp now = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);
        List<?> queriedList = bookingCustomRepository.getAllOrderedIncomingBookingEntitiesOfUser(userId);
        List<BookingEntity> bookingEntities = getBookingEntitiesFromQueriedList(queriedList);
        List<BookingEntity> result = new ArrayList<>();
        int startIndex = itemsPerPage*(page-1);
        int maxIndex = queriedList.size() - 1;
        int endIndex = startIndex + itemsPerPage - 1;
        endIndex = endIndex <= maxIndex ? endIndex : maxIndex;
        if(startIndex > endIndex) return result;
        for(int i = startIndex; i <= endIndex; ++i)
        {
            result.add(bookingEntities.get(i));
        }
        return result;
    }

    private List<BookingEntity> getBookingEntitiesFromQueriedList(List<?> queriedList)
    {
        List<BookingEntity> result = new ArrayList<>();
        if(queriedList != null)
        {
            result = queriedList.stream().map(queriedBooking -> {
                BookingEntity bookingEntity = (BookingEntity) queriedBooking;
                return bookingEntity;
            }).collect(Collectors.toList());
        }
        return result;
    }
    public int countAllIncomingMatchesOfUser(String userId)
    {
        return bookingCustomRepository.countAllIncomingBookingEntityOfUser(userId);
    }
}
