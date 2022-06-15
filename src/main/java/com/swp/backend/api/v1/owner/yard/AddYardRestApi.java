package com.swp.backend.api.v1.owner.yard;

import com.google.gson.Gson;
import com.swp.backend.model.YardModel;
import com.swp.backend.service.SecurityContextService;
import com.swp.backend.service.YardService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/v1/owner/yard")
public class AddYardRestApi {
    YardService yardService;
    SecurityContextService securityContextService;
    Gson gson;

    @PostMapping(value = "add-yard")
    public ResponseEntity<String> createYard(@RequestBody(required = false) YardRequest yardRequest) {
        if (yardRequest == null) {
            return ResponseEntity.badRequest().body("Missing body");
        }
        try {
            SecurityContext context = SecurityContextHolder.getContext();
            String userId = securityContextService.extractUsernameFromContext(context);
            yardService.createNewYard(userId, yardRequest);
            return ResponseEntity.ok("Create yard success!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(value = "")
    public ResponseEntity<String> showAllYard(){
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            String ownerId = securityContextService.extractUsernameFromContext(securityContext);
            List<YardModel> listYard = yardService.getAllYardByOwnerId(ownerId);
            return ResponseEntity.ok().body(gson.toJson(listYard));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
