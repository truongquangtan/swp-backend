package com.swp.backend.api.v1.vote;

import com.swp.backend.model.VoteModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetVoteResponse {
    private int maxResult;
    private int page;
    private List<VoteModel> votes;
}
