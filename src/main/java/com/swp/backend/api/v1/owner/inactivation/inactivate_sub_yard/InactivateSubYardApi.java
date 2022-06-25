package com.swp.backend.api.v1.owner.inactivation.inactivate_sub_yard;

import com.google.gson.Gson;
import com.swp.backend.exception.ErrorResponse;
import com.swp.backend.exception.InactivateProcessException;
import com.swp.backend.model.MessageResponse;
import com.swp.backend.service.InactivationService;
import com.swp.backend.service.ReactivationService;
import com.swp.backend.service.SecurityContextService;
import com.swp.backend.service.SubYardService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.mail.Message;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1/owners/me")
public class InactivateSubYardApi {
    private Gson gson;
    private ReactivationService reactivationService;
    private InactivationService inactivationService;
    private SecurityContextService securityContextService;
    private SubYardService subYardService;

    @DeleteMapping(value = "yards/{yardId}/sub-yards/{subYardId}")
    public ResponseEntity<String> inactivateSubYard(@PathVariable(name = "yardId") String yardId,
                                                    @PathVariable(name = "subYardId") String subYardId)
    {
        try
        {
            SecurityContext context = SecurityContextHolder.getContext();
            String ownerId = securityContextService.extractUsernameFromContext(context);

            String bigYardId = subYardService.getBigYardIdFromSubYard(subYardId);
            if(!bigYardId.equals(yardId))
            {
                ErrorResponse error = ErrorResponse.builder().message("The sub-yard is not in yard.").build();
                return ResponseEntity.badRequest().body(gson.toJson(error));
            }

            inactivationService.inactivateSubYard(ownerId, subYardId);
            MessageResponse response = new MessageResponse("Inactivate successfully");
            return ResponseEntity.ok().body(gson.toJson(response));
        } catch (InactivateProcessException inactivateProcessException)
        {
            ErrorResponse response = ErrorResponse.builder().message(inactivateProcessException.getFilterMessage()).build();
            return ResponseEntity.badRequest().body(gson.toJson(response));
        }
    }
    @PostMapping(value = "/yards/{yardId}/sub-yards/{subYardId}")
    public ResponseEntity<String> reactiveSubYard(@PathVariable(name = "yardId") String yardId,
                                                    @PathVariable(name = "subYardId") String subYardId)
    {
        try
        {
            SecurityContext context = SecurityContextHolder.getContext();
            String ownerId = securityContextService.extractUsernameFromContext(context);

            String bigYardId = subYardService.getBigYardIdFromSubYard(subYardId);
            if(!bigYardId.equals(yardId))
            {
                ErrorResponse error = ErrorResponse.builder().message("The sub-yard is not in yard.").build();
                return ResponseEntity.badRequest().body(gson.toJson(error));
            }

            reactivationService.reactiveSubYard(ownerId, subYardId);
            MessageResponse response = new MessageResponse("Reactivate successfully");
            return ResponseEntity.ok().body(gson.toJson(response));
        } catch (RuntimeException ex)
        {
            ex.printStackTrace();
            ErrorResponse response = ErrorResponse.builder().message(ex.getMessage()).build();
            return ResponseEntity.badRequest().body(gson.toJson(response));
        }
    }
}
