package com.swp.backend.api.v1.owner.inactivation.inactivate_yard;

import com.google.gson.Gson;
import com.swp.backend.exception.ErrorResponse;
import com.swp.backend.exception.InactivateProcessException;
import com.swp.backend.model.MessageResponse;
import com.swp.backend.service.InactivationService;
import com.swp.backend.service.ReactivationService;
import com.swp.backend.service.SecurityContextService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1/owners/me")
public class InactivateYardApi {
    private Gson gson;
    private InactivationService inactivationService;
    private ReactivationService reactivationService;
    private SecurityContextService securityContextService;

    @PutMapping(value = "yards/{yardId}/inactivate")
    public ResponseEntity<String> inactivateYard(@PathVariable String yardId) {
        try {
            SecurityContext context = SecurityContextHolder.getContext();
            String ownerId = securityContextService.extractUsernameFromContext(context);

            inactivationService.inactivateYard(ownerId, yardId);

            MessageResponse response = new MessageResponse("Inactivate successfully");
            return ResponseEntity.ok().body(gson.toJson(response));
        } catch (InactivateProcessException inactivateProcessException) {
            ErrorResponse response = ErrorResponse.builder().message(inactivateProcessException.getFilterMessage()).build();
            return ResponseEntity.badRequest().body(gson.toJson(response));
        }
    }

    @PutMapping(value = "yards/{yardId}/reactivate")
    public ResponseEntity<String> reactivateYard(@PathVariable String yardId) {
        try {
            SecurityContext context = SecurityContextHolder.getContext();
            String ownerId = securityContextService.extractUsernameFromContext(context);

            reactivationService.reactiveYard(ownerId, yardId);

            MessageResponse response = new MessageResponse("Reactivate successfully");
            return ResponseEntity.ok().body(gson.toJson(response));
        } catch (RuntimeException runtimeException) {
            ErrorResponse response = ErrorResponse.builder().message(runtimeException.getMessage()).build();
            return ResponseEntity.badRequest().body(gson.toJson(response));
        }
    }
}
