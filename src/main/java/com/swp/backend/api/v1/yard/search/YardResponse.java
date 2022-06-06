package com.swp.backend.api.v1.yard.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YardResponse {
    private List<YardResponseMember> yards;
    private int page;
    private int maxCount;
}
