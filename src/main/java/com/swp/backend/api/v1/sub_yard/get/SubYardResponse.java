package com.swp.backend.api.v1.sub_yard.get;

import com.swp.backend.entity.SubYardEntity;
import com.swp.backend.model.SubYardModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class SubYardResponse {
    private String message;
    private List<SubYardModel> subYards;
}
