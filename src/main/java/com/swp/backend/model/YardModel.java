package com.swp.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YardModel {
    private String id;
    private String name;
    private String address;
    private String districtName;
    private String province;
    private String openAt;
    private String closeAt;
    private int reference;
    private Timestamp createdAt;
    private List<String> images;
}
