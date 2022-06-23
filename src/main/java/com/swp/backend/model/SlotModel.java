package com.swp.backend.model;

import com.swp.backend.entity.SlotEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SlotModel {
    private int id;
    private int price;
    private String startTime;
    private String endTime;
    private boolean isActive;

    public static SlotModel buildFromSlotEntity(SlotEntity slotEntity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return SlotModel.builder().id(slotEntity.getId())
                .isActive(slotEntity.isActive())
                .startTime(slotEntity.getStartTime().format(formatter))
                .endTime(slotEntity.getEndTime().format(formatter))
                .price(slotEntity.getPrice())
                .build();
    }
}
