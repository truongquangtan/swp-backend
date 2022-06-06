package com.swp.backend.api.v1.yard.search;

import com.swp.backend.entity.YardEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import java.time.LocalTime;

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
