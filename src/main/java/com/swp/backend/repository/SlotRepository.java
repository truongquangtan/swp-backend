package com.swp.backend.repository;

import com.swp.backend.entity.SlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface SlotRepository extends JpaRepository<SlotEntity, String> {
    List<SlotEntity> findSlotEntitiesByRefYardAndActiveIsTrue(String refYard);
    List<SlotEntity> findSlotEntitiesByStartTimeGreaterThanAndRefYardAndActiveIsTrue(LocalTime startTime,String refYard);
    SlotEntity findSlotEntityByIdAndStartTimeGreaterThanAndActive(int slotId, LocalTime startTime, boolean isActive);
    SlotEntity findSlotEntityByIdAndActive(int slotId, boolean isActive);
    SlotEntity findSlotEntityById(int slotId);
}
