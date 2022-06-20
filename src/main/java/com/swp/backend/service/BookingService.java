package com.swp.backend.service;

import com.swp.backend.constance.BookingStatus;
import com.swp.backend.entity.BookingEntity;
import com.swp.backend.entity.SlotEntity;
import com.swp.backend.entity.YardEntity;
import com.swp.backend.model.BookingModel;
import com.swp.backend.myrepository.BookingCustomRepository;
import com.swp.backend.repository.BookingRepository;
import com.swp.backend.repository.SlotRepository;
import com.swp.backend.repository.SubYardRepository;
import com.swp.backend.repository.YardRepository;
import com.swp.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingService {
    private SlotService slotService;
    private SubYardService subYardService;
    private BookingRepository bookingRepository;
    private BookingCustomRepository bookingCustomRepository;
    private YardRepository yardRepository;
    private SlotRepository slotRepository;
    private SubYardRepository subYardRepository;

    @Transactional
    public BookingEntity book(String userId, String yardId, BookingModel bookingModel) {
        String errorNote = "";
        int slotId = bookingModel.getSlotId();

        //Booking date in past filter
        LocalDate bookingDate = LocalDate.parse(bookingModel.getDate(), DateTimeFormatter.ofPattern("d/M/yyyy"));
        LocalDate now = LocalDate.now(ZoneId.of(DateHelper.VIETNAM_ZONE));
        if (bookingDate.compareTo(now) < 0) {
            errorNote = "The date of booking is in the past";
            return processBooking(userId, yardId, bookingModel, errorNote, BookingStatus.FAILED);
        }

        //SubYard filter
        String subYardId = bookingModel.getRefSubYard();
        if (subYardId == null) {
            errorNote = "Cannot find sub yard from slot id " + slotId;
            return processBooking(userId, yardId, bookingModel, errorNote, BookingStatus.FAILED);
        }
        if (!subYardService.isActiveSubYard(subYardId)) {
            errorNote = "SubYard of slot id " + bookingModel.getSlotId() + " is not active";
            return processBooking(userId, yardId, bookingModel, errorNote, BookingStatus.FAILED);
        }

        //Slot Not Available Filter
        Timestamp timestamp = DateHelper.parseFromStringToTimestamp(bookingModel.getDate());
        if (!slotService.isSlotActive(slotId)) {
            errorNote = "Slot of slot id " + slotId + " is not active.";
            return processBooking(userId, yardId, bookingModel, errorNote, BookingStatus.FAILED);
        }
        if (!slotService.isSlotAvailableFromBooking(slotId, timestamp)) {
            errorNote = "Slot of slot id " + slotId + " is booked.";
            return processBooking(userId, yardId, bookingModel, errorNote, BookingStatus.FAILED);
        }

        //Local Time not exceed Start Time
        Timestamp dateRequest = DateHelper.parseFromStringToTimestamp(bookingModel.getDate());
        if (DateHelper.isToday(dateRequest) && !slotService.isSlotExceedTimeToday(slotId)) {
            errorNote = "Slot of slot id " + slotId + " is started";
            return processBooking(userId, yardId, bookingModel, errorNote, BookingStatus.FAILED);
        }

        return processBooking(userId, yardId, bookingModel, errorNote, BookingStatus.SUCCESS);
    }

    @Transactional
    public BookingEntity processBooking(String userId, String yardId, BookingModel bookingModel, String errorNote, String status) {
        Timestamp timestamp = DateHelper.parseFromStringToTimestamp(bookingModel.getDate());
        Timestamp now = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);

        String id = UUID.randomUUID().toString();

        BookingEntity bookingEntity = BookingEntity.builder()
                .id(id)
                .accountId(userId)
                .slotId(bookingModel.getSlotId())
                .date(timestamp)
                .status(status)
                .note(errorNote)
                .price(bookingModel.getPrice())
                .bookAt(now)
                .bigYardId(yardId)
                .subYardId(bookingModel.getRefSubYard())
                .build();

        bookingRepository.save(bookingEntity);

        if(status.equals(BookingStatus.SUCCESS))
        {
            try
            {
                YardEntity yardEntity = yardRepository.findYardEntitiesById(yardId);
                int currentNumberOfBookings = yardEntity.getNumberOfBookings() == null ? 0 : yardEntity.getNumberOfBookings();
                yardEntity.setNumberOfBookings(currentNumberOfBookings + 1);
                yardRepository.save(yardEntity);
            }
            catch (Exception ex)
            {
                throw new RuntimeException("Increase number of booking in yard entity failed.");
            }

        }

        return bookingEntity;
    }

    public List<BookingEntity> getIncomingMatchesOfUser(String userId, int itemsPerPage, int page) {
        List<BookingEntity> incomingMatches = getIncomingMatches(userId);
        List<BookingEntity> result = new ArrayList<>();

        int startIndex = itemsPerPage * (page - 1);
        int maxIndex = incomingMatches.size() - 1;
        int endIndex = startIndex + itemsPerPage - 1;
        endIndex = Math.min(endIndex, maxIndex);

        if (startIndex > endIndex) return result;

        for (int i = startIndex; i <= endIndex; ++i) {
            result.add(incomingMatches.get(i));
        }
        return result;
    }

    private List<BookingEntity> getIncomingMatches(String userId) {
        List<?> queriedListToday = bookingCustomRepository.getAllOrderedIncomingBookingEntitiesOfUserToday(userId);
        List<?> queriedListFutureDate = bookingCustomRepository.getAllOrderedIncomingBookingEntitiesOfUserFutureDate(userId);
        List<BookingEntity> bookingEntities = getBookingEntitiesFromQueriedList(queriedListToday);
        List<BookingEntity> bookingEntitiesFutureDate = getBookingEntitiesFromQueriedList(queriedListFutureDate);
        bookingEntities.addAll(bookingEntitiesFutureDate);
        return bookingEntities;
    }

    private List<BookingEntity> getBookingEntitiesFromQueriedList(List<?> queriedList) {
        List<BookingEntity> result = new ArrayList<>();
        if (queriedList != null) {
            result = queriedList.stream().map(queriedBooking -> {
                return (BookingEntity) queriedBooking;
            }).collect(Collectors.toList());
        }
        return result;
    }

    public List<BookingEntity> getBookingHistoryOfUser(String userId, int itemsPerPage, int page) {
        List<BookingEntity> result = new ArrayList<>();

        int startIndex = itemsPerPage * (page - 1);
        int endIndex = startIndex + itemsPerPage - 1;
        int maxIndex = countAllHistoryBookingsOfUser(userId) - 1;
        endIndex = Math.min(endIndex, maxIndex);

        if (startIndex > endIndex) return result;
        result = bookingCustomRepository.getOrderedBookingEntitiesOfUserByPage(userId, startIndex, endIndex);

        if (result == null) {
            return new ArrayList<>();
        }
        return result;
    }

    public int countAllHistoryBookingsOfUser(String userId) {
        return bookingCustomRepository.countAllHistoryBookingsOfUser(userId);
    }

    public int countAllIncomingMatchesOfUser(String userId) {
        return getIncomingMatches(userId).size();
    }

    public List<BookingEntity> getAllIncomeSlotByOwnerId(String ownerId) {
        List<String> listYardId = yardRepository.getAllYardIdByOwnerId(ownerId);
        List<String> listSubYardId = subYardRepository.getAllSubYardIdByListBigYardId(listYardId);
        List<SlotEntity> listSlot = slotRepository.getAllSlotsByListSubYardId(listSubYardId);
        List<Integer> listSlotId = listSlot.parallelStream().map(SlotEntity::getId).collect(Collectors.toList());
        return bookingRepository.getListSlotExitsBookingReference(listSlotId);
    }

    public BookingEntity getBookingById(int id){
        return bookingRepository.getById(id);
    }
}
