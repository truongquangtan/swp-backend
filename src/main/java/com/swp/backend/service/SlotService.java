package com.swp.backend.service;

import com.swp.backend.entity.SlotEntity;
import com.swp.backend.model.Slot;
import com.swp.backend.model.model_builder.ListSlotBuilder;
import com.swp.backend.myrepository.SlotCustomRepository;
import com.swp.backend.repository.SlotRepository;
import com.swp.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.List;

@Service
@AllArgsConstructor
public class SlotService {
    private SlotCustomRepository slotCustomRepository;
    private SlotRepository slotRepository;

    public List<Slot> getAllSlotInSubYardByDate(String subYardId, String date)
    {
        List<Slot> allSlots;
        List<Slot> bookedSlots;

        if(DateHelper.isToday(DateHelper.parseFromStringToDate(date)))
        {
            allSlots = getAllSlotsInSubYardByToday(subYardId);
            bookedSlots = getBookedSlotsInSubYardByToday(subYardId, date);
        }
        else
        {
            allSlots = getAllSlotsInSubYardByFutureDate(subYardId);
            bookedSlots = getBookedSlotsInSubYardByFutureDate(subYardId, date);
        }

        allSlots = updateBookedStateOfAllSlots(allSlots, bookedSlots);

        return allSlots;

    }
    public List<Slot> getAllSlotsInSubYardByToday(String subYardId)
    {
        Timestamp now = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);
        LocalTime localTime = DateHelper.getLocalTimeFromTimeStamp(now);
        List<SlotEntity> allSlotEntities = slotRepository.findSlotEntitiesByStartTimeGreaterThanAndRefYardAndActiveIsTrue(localTime, subYardId);
        return ListSlotBuilder.getAvailableSlotsFromSlotEntities(allSlotEntities);
    }
    public List<Slot> getAllSlotsInSubYardByFutureDate(String subYardId)
    {
        List<SlotEntity> allSlotEntities = slotRepository.findSlotEntitiesByRefYardAndActiveIsTrue(subYardId);
        return ListSlotBuilder.getAvailableSlotsFromSlotEntities(allSlotEntities);
    }
    public List<Slot> getBookedSlotsInSubYardByToday(String subYardId, String date)
    {
        Timestamp timestampFromDate = DateHelper.parseFromStringToTimestamp(date);
        Timestamp now = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);
        LocalTime localTimeNow = DateHelper.getLocalTimeFromTimeStamp(now);
        List<?> queriedSlots = slotCustomRepository.getAllBookedSlotInSubYardByToday(subYardId, timestampFromDate, localTimeNow);

        return ListSlotBuilder.getBookedSlotsFromQueriedSlotEntities(queriedSlots);
    }
    public List<Slot> getBookedSlotsInSubYardByFutureDate(String subYardId, String date)
    {
        Timestamp timestamp = DateHelper.parseFromStringToTimestampOfDate(date);
        List<?> queriedSlots = slotCustomRepository.getAllBookedSlotInSubYardByFutureDate(subYardId, timestamp);
        return ListSlotBuilder.getBookedSlotsFromQueriedSlotEntities(queriedSlots);
    }

    private List<Slot> updateBookedStateOfAllSlots(List<Slot> allSlots, List<Slot> bookedSlots)
    {
        bookedSlots.forEach(slot -> {
            int pos = -1;
            for(int i = 0; i < allSlots.size(); ++i)
            {
                if(allSlots.get(i).getId() == slot.getId())
                {
                    pos = i;
                    break;
                }
            }
            if(pos != -1) {
                allSlots.get(pos).setBooked(true);
            }
        });
        return allSlots;
    }

}
