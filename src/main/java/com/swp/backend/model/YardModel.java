package com.swp.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YardModel {
    protected String id;
    protected String name;
    protected String address;
    protected String districtName;
    protected String openAt;
    protected String closeAt;
    protected List<String> images;
}
