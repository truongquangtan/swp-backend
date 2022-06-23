package com.swp.backend.api.v1.vote;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetVoteRequest {
    private Integer itemsPerPage;
    private Integer page;
}
