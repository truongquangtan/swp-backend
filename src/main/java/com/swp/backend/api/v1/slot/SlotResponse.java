package com.swp.backend.api.v1.slot;

import com.swp.backend.entity.SlotEntity;
import com.swp.backend.model.Slot;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class SlotResponse {
    List<Slot> slots;
}
