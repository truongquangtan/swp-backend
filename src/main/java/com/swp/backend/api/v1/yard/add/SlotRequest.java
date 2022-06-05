package com.swp.backend.api.v1.yard.add;


import lombok.Data;

import java.time.LocalTime;
@Data
public class SlotRequest {
    private LocalTime startTime;
    private LocalTime endTime;
    private int price;
};
