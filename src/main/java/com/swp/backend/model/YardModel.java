package com.swp.backend.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class YardModel {
    private String id;
    private String name;
    private String address;
    private String districtName;
    private String openAt;
    private String closeAt;
    private List<String> images;
}
