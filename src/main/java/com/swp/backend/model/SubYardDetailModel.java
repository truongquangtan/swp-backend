package com.swp.backend.model;

import com.swp.backend.entity.SlotEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SubYardDetailModel {
    private String id;
    private String name;
    private String typeYard;
    private String createAt;
    private boolean isActive;
    private List<SlotModel> slots;
}
