package com.swp.backend.api.v1.owner.yard;

import lombok.Data;

import java.util.List;

@Data
public class SubYardRequest {
    private String name;
    private String type;
    private List<SlotRequest> slots;
}
