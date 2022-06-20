package com.swp.backend.api.v1.vote;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoteRequest {
    private String postId;
    private String bookingId;
    private Integer score;
    private String comment;
}
