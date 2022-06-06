package com.swp.backend.api.v1.yard.search;

import com.swp.backend.entity.YardEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YardResponse {
    private List<YardResponseMember> Yards;
    private int page;
    private int maxCount;
}
