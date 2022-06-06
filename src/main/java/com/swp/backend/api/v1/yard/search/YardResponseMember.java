package com.swp.backend.api.v1.yard.search;

import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class YardResponseMember {
    private String id;
    private String name;
    private String address;
    private int districtId;
    private LocalTime openAt;
    private LocalTime closeAt;
    private List<String> images;
}
