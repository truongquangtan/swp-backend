package com.swp.backend.api.v1.slot;

import com.swp.backend.model.Slot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SlotResponse {
    private String message;
    private List<Slot> data;
}
