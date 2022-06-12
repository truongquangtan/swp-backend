package com.swp.backend.api.v1.vote;

import com.google.gson.Gson;
import com.swp.backend.exception.ErrorResponse;
import com.swp.backend.service.SecurityContextService;
import com.swp.backend.service.VoteService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/")
@AllArgsConstructor
public class VoteRestApi {

    private VoteService voteService;
    private SecurityContextService securityContextService;
    private Gson gson;

    @PostMapping(value = "vote")
    public ResponseEntity<String> postVote(@RequestBody(required = false) PostVoteRequest voteRequest) {
        try {
            if (voteRequest == null) {
                ErrorResponse errorResponse = ErrorResponse.builder().message("Missing body.").build();
                return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
            }
            SecurityContext context = SecurityContextHolder.getContext();
            String userId = securityContextService.extractUsernameFromContext(context);
            boolean postVote = voteService.postVote(
                    userId,
                    voteRequest.getSubYarId(),
                    voteRequest.getScore(),
                    voteRequest.getComment()
            );

            if (postVote) {
                return ResponseEntity.ok("Vote success!");
            } else {
                ErrorResponse errorResponse = ErrorResponse.builder().message("Server busy can't handle this vote.").build();
                return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            ErrorResponse errorResponse = ErrorResponse.builder().message("Server busy can't handle this request!").build();
            return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
        }
    }
}
