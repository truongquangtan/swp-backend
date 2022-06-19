package com.swp.backend.api.v1.owner.yard.request;

import lombok.Data;

import java.util.List;

@Data
public class SubYardRequest {
    private String name;
    private Integer type;
    private List<SlotRequest> slots;
}
