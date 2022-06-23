package com.swp.backend.api.v1.sub_yard.get_by_owner;

import com.google.gson.Gson;
import com.swp.backend.exception.ErrorResponse;
import com.swp.backend.service.SecurityContextService;
import com.swp.backend.service.SubYardService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping(value = "api/v1/owners")
public class GetSubYardDetailApi {
    private Gson gson;
    private SecurityContextService securityContextService;
    private SubYardService subYardService;

    @GetMapping(value = "/me/yards/{yardId}/sub-yards/{subYardId}")
    public ResponseEntity<String> getSubYardDetailById(@PathVariable("yardId") String yardId, @PathVariable("subYardId") String subYardId) {
        try {
            GetSubYardDetailResponse response;
            SecurityContext securityContext = SecurityContextHolder.getContext();
            String ownerId = securityContextService.extractUsernameFromContext(securityContext);

            response = subYardService.getSubYardDetailResponse(ownerId, yardId, subYardId);

            return ResponseEntity.ok().body(gson.toJson(response));
        } catch (RuntimeException runtimeException) {
            ErrorResponse error = ErrorResponse.builder().message(runtimeException.getMessage()).build();
            return ResponseEntity.badRequest().body(gson.toJson(error));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
