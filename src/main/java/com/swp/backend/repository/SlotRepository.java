package com.swp.backend.repository;

import com.swp.backend.entity.SlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface SlotRepository extends JpaRepository<SlotEntity, String> {
    public List<SlotEntity> findSlotEntitiesByRefYardAndActiveIsTrue(String refYard);

    public List<SlotEntity> findSlotEntitiesByStartTimeGreaterThanAndRefYardAndActiveIsTrue(LocalTime startTime, String refYard);

    public SlotEntity findSlotEntityByIdAndStartTimeGreaterThanAndActive(int slotId, LocalTime startTime, boolean isActive);

    public SlotEntity findSlotEntityByIdAndActive(int slotId, boolean isActive);

    @Query("SELECT slot FROM SlotEntity slot WHERE slot.refYard IN :listSubYardId")
    public List<SlotEntity> getAllSlotsByListSubYardId(@Param("listSubYardId") Collection<String> listSubYardId);

    @Query("SELECT slot FROM SlotEntity slot WHERE slot.id IN :listSlotId")
    public List<SlotEntity> getAllSlotEntityByListSlotId(@Param("listSlotId") Collection<String> listSlotId);
}
