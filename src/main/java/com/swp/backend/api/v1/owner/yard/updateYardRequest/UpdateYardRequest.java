package com.swp.backend.api.v1.owner.yard.updateYardRequest;

import com.swp.backend.api.v1.owner.yard.request.SubYardRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.sql.Update;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UpdateYardRequest {
    private String name;
    private String address;
    private Integer districtId;
    private String openAt;
    private String closeAt;
    private String slotDuration;
    private List<UpdateSubYardRequest> subYards;
}
