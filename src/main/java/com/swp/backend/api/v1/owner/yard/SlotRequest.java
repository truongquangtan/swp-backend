package com.swp.backend.api.v1.owner.yard;


import lombok.Data;

@Data
public class SlotRequest {
    private String startTime;
    private String endTime;
    private int price;
}
