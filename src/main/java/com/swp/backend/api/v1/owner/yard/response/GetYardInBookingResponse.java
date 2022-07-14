package com.swp.backend.api.v1.owner.yard.response;

import com.swp.backend.model.SubYardModel;
import com.swp.backend.model.SubYardSimpleModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetYardInBookingResponse {
    private String yardId;
    private String yardName;
    private List<SubYardSimpleModel> subYards;
}
