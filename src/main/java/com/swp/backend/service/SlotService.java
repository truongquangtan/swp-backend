package com.swp.backend.service;

import com.swp.backend.constance.BookingStatus;
import com.swp.backend.entity.SlotEntity;
import com.swp.backend.model.Slot;
import com.swp.backend.model.model_builder.ListSlotBuilder;
import com.swp.backend.myrepository.SlotCustomRepository;
import com.swp.backend.repository.BookingRepository;
import com.swp.backend.repository.SlotRepository;
import com.swp.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@AllArgsConstructor
public class SlotService {
    private SlotCustomRepository slotCustomRepository;
    private SlotRepository slotRepository;
    private BookingRepository bookingRepository;

    public List<Slot> getAllSlotInSubYardByDate(String subYardId, String date) {
        try {

            LocalDate today = LocalDate.now(ZoneId.of(DateHelper.VIETNAM_ZONE));
            LocalDate queryDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("d/M/yyyy"));

            if (queryDate.compareTo(today) < 0) {
                return null;
            }

            List<Slot> allSlots = getAllSlotsInSubYardByDate(subYardId, today, queryDate);
            List<Slot> bookedSlots = getBookedSlotsInSubYardByDate(subYardId, today, queryDate);

            return updateBookedStateOfAllSlots(allSlots, bookedSlots);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //Get all slots by date
    private List<Slot> getAllSlotsInSubYardByDate(String subYardId, LocalDate today, LocalDate queryDate) {
        List<SlotEntity> allSlotEntities;
        if (today.compareTo(queryDate) == 0) {
            LocalTime now = LocalTime.now(ZoneId.of(DateHelper.VIETNAM_ZONE));
            allSlotEntities = slotRepository.findSlotEntitiesByStartTimeGreaterThanAndRefYardAndActiveIsTrue(now, subYardId);
        } else {
            allSlotEntities = slotRepository.findSlotEntitiesByRefYardAndActiveIsTrue(subYardId);
        }
        return ListSlotBuilder.getAvailableSlotsFromSlotEntities(allSlotEntities);
    }

    //Get slot booked by date
    private List<Slot> getBookedSlotsInSubYardByDate(String subYardId, LocalDate today, LocalDate queryDate) {
        List<?> queriedSlots = slotCustomRepository.getAllBookedSlotInSubYardByDate(subYardId, today, queryDate);
        return ListSlotBuilder.getBookedSlotsFromQueriedSlotEntities(queriedSlots);
    }

    //Merge and update state of slots
    private List<Slot> updateBookedStateOfAllSlots(List<Slot> allSlots, List<Slot> bookedSlots) {
        bookedSlots.forEach(slot -> {
            int pos = -1;
            for (int i = 0; i < allSlots.size(); ++i) {
                if (allSlots.get(i).getId() == slot.getId()) {
                    pos = i;
                    break;
                }
            }
            if (pos != -1) {
                allSlots.get(pos).setBooked(true);
            }
        });
        return allSlots;
    }

    public String getSubYardIdFromSlotId(int slotId) {
        return slotCustomRepository.findSubYardIdFromSlotId(slotId);
    }

    public boolean isSlotAvailableFromBooking(int slotId, Timestamp timestamp) {
        return bookingRepository.getBookingEntityBySlotIdAndStatusAndDate(slotId, BookingStatus.SUCCESS, timestamp) == null;
    }

    public boolean isSlotActive(int slotId) {
        return slotRepository.findSlotEntityByIdAndActive(slotId, true) != null;
    }

    public boolean isSlotExist(int slotId) {
        return slotRepository.findSlotEntityById(slotId) != null;
    }

    public boolean isSlotExceedTimeToday(int slotId) {
        Timestamp now = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);
        LocalTime localTimeToday = DateHelper.getLocalTimeFromTimeStamp(now);
        SlotEntity slotEntity = slotRepository.findSlotEntityByIdAndStartTimeGreaterThanAndActive(slotId, localTimeToday, true);
        return slotEntity != null;
    }
}
