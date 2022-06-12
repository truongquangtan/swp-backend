package com.swp.backend.api.v1.vote;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostVoteRequest {
    private String subYarId;
    private int score;
    private String comment;
}
