package com.swp.backend.api.v1.yard.add;

import com.google.gson.Gson;
import com.swp.backend.service.SecurityContextService;
import com.swp.backend.service.YardService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1/yard")
public class AddYardRestApi {
    YardService yardService;
    SecurityContextService securityContextService;
    Gson gson;

    @PostMapping(value = "add-yard")
    public ResponseEntity<String> createYard(@RequestBody(required = false) YardRequest yardRequest){
        if(yardRequest == null){
            return ResponseEntity.badRequest().body("Missing body");
        }
        try {
            SecurityContext context = SecurityContextHolder.getContext();
            String userId = securityContextService.extractUsernameFromContext(context);
//            YardEntity yard = yardService.createNewYard(userId,
//                    yardRequest.getName(),
//                    yardRequest.getAddress(),
//                    yardRequest.getDistrictId(),
//                    yardRequest.getOpenAt(),
//                    yardRequest.getCloseAt(),
//                    yardRequest.getSlotDuration()
//            );
            System.out.println(gson.toJson(yardRequest));

            List<SubYardRequest> listSubYard = yardRequest.getListSubYard();
            if(listSubYard != null && listSubYard.size() > 0){
                listSubYard.forEach(subYard -> {
                    System.out.println(gson.toJson(subYard));
                });
            }
            return ResponseEntity.ok("Create yard success!");
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }
}
