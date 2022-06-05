package com.swp.backend.api.v1.yard.add;

import lombok.Data;

import java.util.List;
@Data
public class SubYardRequest {
    private String name;
    private String address;
    private String type;
    private List<String> images;
    private List<SlotRequest> slots;
}
