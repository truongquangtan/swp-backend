package com.swp.backend.api.v1.owner.inactivation.inactivate_sub_yard;

import com.swp.backend.model.SubYardDetailModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SubYardsResponse {
    private String message;
    private List<SubYardDetailModel> subYards;
}
