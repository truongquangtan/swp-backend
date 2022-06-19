package com.swp.backend.api.v1.owner.yard;

import com.google.gson.Gson;
import com.swp.backend.service.SecurityContextService;
import com.swp.backend.service.YardService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/v1/owners")
public class YardRestApi {
    private YardService yardService;
    private SecurityContextService securityContextService;
    private Gson gson;

    @PostMapping(value = "me/yards")
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

    @PostMapping(value = "{ownerId}/yards/search")
    public ResponseEntity<String> showAllYard(@RequestBody(required = false) GetYardRequest getYardRequest, @PathVariable String ownerId) {
        try {
            GetYardResponse response;
            if(getYardRequest == null){
                response = yardService.findAllYardByOwnerId(ownerId, null, null);
            }else {
                response = yardService.findAllYardByOwnerId(ownerId, getYardRequest.getItemsPerPage(), getYardRequest.getPage());
            }
            return ResponseEntity.ok().body(gson.toJson(response));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
