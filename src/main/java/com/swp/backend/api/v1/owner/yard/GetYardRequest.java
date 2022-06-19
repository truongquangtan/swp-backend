package com.swp.backend.api.v1.owner.yard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetYardRequest {
    private int itemsPerPage;
    private int page;
}
