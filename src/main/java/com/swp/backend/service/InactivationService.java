package com.swp.backend.service;

import com.swp.backend.entity.BookingEntity;
import com.swp.backend.entity.SlotEntity;
import com.swp.backend.entity.SubYardEntity;
import com.swp.backend.entity.YardEntity;
import com.swp.backend.exception.InactivateProcessException;
import com.swp.backend.myrepository.BookingCustomRepository;
import com.swp.backend.myrepository.SlotCustomRepository;
import com.swp.backend.myrepository.SubYardCustomRepository;
import com.swp.backend.repository.SlotRepository;
import com.swp.backend.repository.SubYardRepository;
import com.swp.backend.repository.YardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class InactivationService {

    private SlotCustomRepository slotCustomRepository;
    private SubYardCustomRepository subYardCustomRepository;
    private BookingCustomRepository bookingCustomRepository;
    private SlotRepository slotRepository;
    private SubYardRepository subYardRepository;
    private YardRepository yardRepository;
    private CancelBookingService cancelBookingService;
    private SlotService slotService;

    public static final String INACTIVE_SLOT_REASON = "The slot is disabled by owner";
    public static final String INACTIVE_SUB_YARD_REASON = "The sub yard of this slot is disabled by owner";
    public static final String INACTIVE_YARD_REASON = "The yard of this slot is disabled by owner";

    @Transactional(rollbackFor = InactivateProcessException.class)
    public void inactivateSlot(String ownerId, int slotId)
    {
        if(!slotCustomRepository.findOwnerIdFromSlotId(slotId).equals(ownerId))
        {
            throw new InactivateProcessException("The owner is not author of this slot.");
        }

        if(slotRepository.findSlotEntityByIdAndActive(slotId, false) != null)
        {
            throw new InactivateProcessException("The slot is already inactive.");
        }

        try
        {
            processInactivateSlot(slotId, INACTIVE_SLOT_REASON);
        }
        catch (Exception ex)
        {
            throw new InactivateProcessException("Error in canceling process");
        }
    }

    private void processInactivateSlot(int slotId, String reason)
    {
        List<BookingEntity> bookingEntities = bookingCustomRepository.getAllBookingEntitiesOfSlotInFuture(slotId);

        for(BookingEntity bookingEntity : bookingEntities)
        {
            cancelBookingService.cancelBookingProcess(bookingEntity, reason);
            cancelBookingService.sendMailCancelToUser(bookingEntity, reason);
        }

        slotService.inactivateSlot(slotId);
    }
    @Transactional(rollbackFor = InactivateProcessException.class)
    public void inactivateSubYard(String ownerId, String subYardId)
    {
        if(!subYardCustomRepository.getOwnerIdOfSubYard(subYardId).equals(ownerId))
        {
            throw new InactivateProcessException("The owner is not author of this sub-yard.");
        }
        if(subYardRepository.getSubYardEntityByIdAndActive(subYardId, true) == null)
        {
            throw new InactivateProcessException("The sub-yard is already inactive.");
        }

        try
        {
            processInactivateSubYard(subYardId);
        }
        catch (Exception ex)
        {
            throw new InactivateProcessException("Error in canceling process");
        }
    }

    private void processInactivateSubYard(String subYardId)
    {
        List<SlotEntity> slotEntitiesOfSubYard = slotRepository.findSlotEntitiesByRefYardAndActiveIsTrue(subYardId);
        for(SlotEntity slotEntity : slotEntitiesOfSubYard)
        {
            processInactivateSlot(slotEntity.getId(), INACTIVE_SUB_YARD_REASON);
        }

        SubYardEntity subYardEntity = subYardRepository.getSubYardEntitiesById(subYardId);
        subYardEntity.setActive(false);
        subYardRepository.save(subYardEntity);
    }

    @Transactional(rollbackFor = InactivateProcessException.class)
    public void inactivateYard(String ownerId, String yardId)
    {
        yardFilter(ownerId, yardId);
        try
        {
            processInactivateYard(yardId);
        }
        catch(Exception ex)
        {
            throw new InactivateProcessException("Error when process inactivate yard.");
        }
    }

    private void yardFilter(String ownerId, String yardId)
    {
        YardEntity yardEntity = yardRepository.findYardEntitiesById(yardId);
        if(yardEntity == null)
        {
            throw new InactivateProcessException("The yard is not exist.");
        }
        if(!yardEntity.getOwnerId().equals(ownerId))
        {
            throw new InactivateProcessException("The owner is not author of this yard.");
        }
        if(yardRepository.findYardEntityByIdAndActiveAndDeleted(yardId, true, false) == null)
        {
            throw new InactivateProcessException("The yard is already inactive or deleted.");
        }
    }

    private void processInactivateYard(String yardId)
    {
        List<String> listYard = new ArrayList<>();
        listYard.add(yardId);
        List<String> subYardIdList = subYardRepository.getAllSubYardIdByListBigYardId(listYard);
        for(String subYardId : subYardIdList)
        {
            processInactivateSubYard(subYardId);
        }

        YardEntity yardEntity = yardRepository.findYardEntitiesById(yardId);
        yardEntity.setActive(false);
        yardRepository.save(yardEntity);
    }

    public void deleteYard(String ownerId, String yardId)
    {
        yardFilter(ownerId, yardId);
        try
        {
            processInactivateYard(yardId);
        }
        catch(Exception ex)
        {
            throw new InactivateProcessException("Error when process inactivate yard.");
        }
    }

    private void processDeleteYard(String yardId)
    {
        List<String> listYard = new ArrayList<>();
        listYard.add(yardId);
        List<String> subYardIdList = subYardRepository.getAllSubYardIdByListBigYardId(listYard);
        for(String subYardId : subYardIdList)
        {
            processInactivateSubYard(subYardId);
        }

        YardEntity yardEntity = yardRepository.findYardEntitiesById(yardId);
        yardEntity.setDeleted(true);
        yardRepository.save(yardEntity);
    }
}
