package com.swp.backend.api.v1.sub_yard.get;

import com.swp.backend.model.SubYardModel;
import com.swp.backend.model.YardModel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SubYardResponse {
    private String message;
    private YardModel bigYard;
    private List<SubYardModel> subYards;
}
