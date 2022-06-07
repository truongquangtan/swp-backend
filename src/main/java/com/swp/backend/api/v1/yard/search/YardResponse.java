package com.swp.backend.api.v1.yard.search;

import com.swp.backend.model.YardModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class YardResponse {
    private List<YardModel> yards;
    private int page;
    private int maxResult;
}
