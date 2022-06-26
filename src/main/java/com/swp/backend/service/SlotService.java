package com.swp.backend.service;

import com.swp.backend.constance.BookingStatus;
import com.swp.backend.entity.SlotEntity;
import com.swp.backend.exception.InactivateProcessException;
import com.swp.backend.model.Slot;
import com.swp.backend.model.model_builder.ListSlotBuilder;
import com.swp.backend.myrepository.SlotCustomRepository;
import com.swp.backend.repository.BookingRepository;
import com.swp.backend.repository.SlotRepository;
import com.swp.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SlotService {
    private SlotCustomRepository slotCustomRepository;
    private SlotRepository slotRepository;
    private BookingRepository bookingRepository;

    public List<Slot> getAllSlotInSubYardByDate(String subYardId, String date) {
        try {
            LocalDate queryDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("d/M/yyyy"));
            LocalDate now = LocalDate.now(ZoneId.of(DateHelper.VIETNAM_ZONE));
            if (queryDate.compareTo(now) < 0) {
                return new ArrayList<>();
            }

            List<Slot> allSlots = getAllSlotsInSubYardByDate(subYardId, queryDate);
            List<Slot> bookedSlots = getBookedSlotsInSubYardByDate(subYardId, queryDate);

            return updateBookedStateOfAllSlots(allSlots, bookedSlots);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Slot> getAllSlotsInSubYardByDate(String subYardId, LocalDate queryDate) {
        List<SlotEntity> allSlotEntities;
        allSlotEntities = DateHelper.isToday(queryDate) ? getAllSlotsInSubYardToday(subYardId) : getAllSlotsInSubYardByFutureDate(subYardId);
        return ListSlotBuilder.getAvailableSlotsFromSlotEntities(allSlotEntities);
    }

    private List<SlotEntity> getAllSlotsInSubYardByFutureDate(String subYardId) {
        return slotRepository.findSlotEntitiesByRefYardAndActiveIsTrue(subYardId);
    }

    private List<SlotEntity> getAllSlotsInSubYardToday(String subYardId) {
        LocalTime now = LocalTime.now(ZoneId.of(DateHelper.VIETNAM_ZONE));
        return slotRepository.findSlotEntitiesByStartTimeGreaterThanAndRefYardAndActiveIsTrue(now, subYardId);
    }

    private List<Slot> getBookedSlotsInSubYardByDate(String subYardId, LocalDate queryDate) {
        List<?> queriedSlots;
        if (DateHelper.isToday(queryDate)) {
            queriedSlots = slotCustomRepository.getAllBookedSlotInSubYardToday(subYardId);
        } else {
            queriedSlots = slotCustomRepository.getAllBookedSlotInSubYardByFutureDate(subYardId, queryDate);
        }
        return ListSlotBuilder.getBookedSlotsFromQueriedSlotEntities(queriedSlots);
    }

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

    @Transactional
    public void inactivateSlot(int slotId)
    {
        SlotEntity slotEntity = slotRepository.findSlotEntityById(slotId);
        slotEntity.setActive(false);
        slotRepository.save(slotEntity);
    }
    @Transactional
    public void reactivateSlot(int slotId)
    {
        SlotEntity slotEntity = slotRepository.findSlotEntityById(slotId);
        slotEntity.setActive(true);
        slotRepository.save(slotEntity);
    }
    @Transactional
    public void setIsParentActiveFalse(int slotId)
    {
        SlotEntity slotEntity = slotRepository.findSlotEntityById(slotId);
        slotEntity.setParentActive(false);
        slotRepository.save(slotEntity);
    }
    @Transactional
    public void setIsParentActiveTrue(int slotId)
    {
        SlotEntity slotEntity = slotRepository.findSlotEntityById(slotId);
        slotEntity.setParentActive(true);
        slotRepository.save(slotEntity);
    }
    public boolean isSlotAvailableFromBooking(int slotId, Timestamp timestamp) {
        LocalDate localDate = DateHelper.parseFromTimestampToLocalDate(timestamp);
        Timestamp startTime = Timestamp.valueOf(localDate.toString() + " 00:00:00");
        Timestamp endTime = Timestamp.valueOf(localDate.toString() + " 23:59:00");
        return bookingRepository.getBookingEntityBySlotIdAndStatusAndDateIsGreaterThanEqualAndDateIsLessThanEqual(slotId, BookingStatus.SUCCESS, startTime, endTime) == null;
    }

    public boolean isSlotActive(int slotId) {
        return slotRepository.findSlotEntityByIdAndActiveAndParentActive(slotId, true, true) != null;
    }

    public boolean isSlotExceedTimeToday(int slotId) {
        LocalTime localTimeToday = LocalTime.now(ZoneId.of(DateHelper.VIETNAM_ZONE));
        SlotEntity slotEntity = slotRepository.findSlotEntityByIdAndStartTimeGreaterThanAndActive(slotId, localTimeToday, true);
        return slotEntity != null;
    }
}
