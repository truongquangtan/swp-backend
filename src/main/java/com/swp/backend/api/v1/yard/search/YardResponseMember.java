package com.swp.backend.api.v1.yard.search;

import lombok.Data;
import org.apache.tomcat.jni.Local;

import java.time.LocalTime;
import java.util.List;

@Data
public class YardResponseMember {
    private String id;
    private String name;
    private String address;
    private int districtId;
    private String openAt;
    private String closeAt;
    private List<String> images;
}
