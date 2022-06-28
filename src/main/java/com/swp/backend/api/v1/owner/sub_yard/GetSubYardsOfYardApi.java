package com.swp.backend.api.v1.owner.sub_yard;

import com.google.gson.Gson;
import com.swp.backend.exception.ErrorResponse;
import com.swp.backend.model.SubYardModel;
import com.swp.backend.service.SecurityContextService;
import com.swp.backend.service.SubYardService;
import com.swp.backend.service.YardService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(value = "api/v1/owners/me")
@AllArgsConstructor
public class GetSubYardsOfYardApi {
    private Gson gson;
    private SecurityContextService securityContextService;
    private SubYardService subYardService;
    private YardService yardService;

    @GetMapping(value = "yards/{yardId}/subYards")
    public ResponseEntity<String> getSubYardsOfYard(@PathVariable String yardId)
    {
        try
        {
            SecurityContext context = SecurityContextHolder.getContext();
            String ownerId = securityContextService.extractUsernameFromContext(context);

            if(!yardService.getOwnerIdOfYard(yardId).equals(ownerId))
            {
                ErrorResponse error = ErrorResponse.builder().message("The owner is not author of this yard").build();
                return ResponseEntity.badRequest().body(gson.toJson(error));
            }

            List<SubYardModel> subYardModels = subYardService.getSubYardsByBigYard(yardId);
            GetSubYardResponse response = GetSubYardResponse.builder().message("Get sub-yards successfully")
                    .subYards(subYardModels)
                    .build();
            return ResponseEntity.ok().body(gson.toJson(response));
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().body("Error in server: " + ex.getMessage());
        }
    }
}
