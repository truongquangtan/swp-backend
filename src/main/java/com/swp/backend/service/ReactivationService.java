package com.swp.backend.service;

import com.swp.backend.entity.SlotEntity;
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
public class ReactivationService {
    private SlotCustomRepository slotCustomRepository;
    private SubYardCustomRepository subYardCustomRepository;
    private BookingCustomRepository bookingCustomRepository;
    private SlotRepository slotRepository;
    private SubYardRepository subYardRepository;
    private YardRepository yardRepository;
    private CancelBookingService cancelBookingService;
    private SlotService slotService;
    private SubYardService subYardService;
    private YardService yardService;

    @Transactional(rollbackFor = RuntimeException.class)
    public void reactivateSlot(String ownerId, int slotId)
    {
        if(!slotCustomRepository.findOwnerIdFromSlotId(slotId).equals(ownerId))
        {
            throw new RuntimeException("The owner is not author of this slot.");
        }

        if(slotRepository.findSlotEntityByIdAndActive(slotId, false) == null)
        {
            throw new RuntimeException("Can not find inactive slot.");
        }

        try
        {
            slotService.reactivateSlot(slotId);
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Error in canceling process");
        }
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void reactiveSubYard(String ownerId, String subYardId)
    {
        if(!subYardCustomRepository.getOwnerIdOfSubYard(subYardId).equals(ownerId))
        {
            throw new RuntimeException("The owner is not author of this sub-yard.");
        }
        if(subYardRepository.getSubYardEntityByIdAndActive(subYardId, false) == null)
        {
            throw new RuntimeException("Cannot find inactive sub-yard.");
        }

        try
        {
            processReactivateSubYard(subYardId);
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Error in canceling process");
        }
    }
    private void processReactivateSubYard(String subYardId)
    {
        setParentActiveForAllSlotsInSubYard(subYardId);
        subYardService.setIsActiveTrueForSubYard(subYardId);
    }

    private void setParentActiveForAllSlotsInSubYard(String subYardId)
    {
        List<SlotEntity> slotEntitiesOfSubYard = slotRepository.findSlotEntitiesByRefYardAndActiveIsTrue(subYardId);
        if(slotEntitiesOfSubYard != null)
        {
            for(SlotEntity slotEntity : slotEntitiesOfSubYard)
            {
                slotService.setIsParentActiveTrue(slotEntity.getId());
            }
        }
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void reactiveYard(String ownerId, String yardId)
    {
        YardEntity yardEntity = yardRepository.findYardEntitiesById(yardId);
        if(yardEntity == null)
        {
            throw new RuntimeException("The yard is not exist.");
        }
        if(!yardEntity.getOwnerId().equals(ownerId))
        {
            throw new RuntimeException("The owner is not author of this yard.");
        }
        if(yardRepository.findYardEntityByIdAndActiveAndDeleted(yardId, false, false) == null)
        {
            throw new RuntimeException("The yard is already active.");
        }

        try
        {
            List<String> listYard = new ArrayList<>();
            listYard.add(yardId);
            List<String> subYardIdList = subYardRepository.getAllSubYardIdByListBigYardId(listYard);

            if(subYardIdList != null)
            {
                for(String subYardId : subYardIdList)
                {
                    setParentActiveForAllSlotsInSubYard(subYardId);
                    subYardService.setIsParentActiveTrueForSubYard(subYardId);
                }
            }

            yardService.setIsActiveTrueForYard(yardId);
        }
        catch(Exception ex)
        {
            throw new RuntimeException("Error when process inactivate yard.");
        }
    }
}
