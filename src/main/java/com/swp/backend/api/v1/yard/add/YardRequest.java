package com.swp.backend.api.v1.yard.add;

import lombok.Data;

import java.time.LocalTime;
import java.util.List;
@Data
public class YardRequest {
    private String name;
    private String address;
    private int districtId;
    private LocalTime openAt;
    private LocalTime closeAt;
    private int slotDuration;
    private List<SubYardRequest> listSubYard;
}
