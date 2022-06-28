package com.swp.backend.api.v1.owner.sub_yard;

import com.swp.backend.model.SubYardModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GetSubYardResponse {
    private String message;
    private List<SubYardModel> subYards;
}
