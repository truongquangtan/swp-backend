package com.swp.backend.api.v1.owner.yard;

import lombok.Data;

import java.util.List;

@Data
public class YardRequest {
    private String name;
    private String address;
    private Integer districtId;
    private String openAt;
    private String closeAt;
    private int slotDuration;
    private List<SubYardRequest> subYards;
}
