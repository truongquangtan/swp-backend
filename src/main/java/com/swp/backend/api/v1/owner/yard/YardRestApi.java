package com.swp.backend.api.v1.owner.yard;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.swp.backend.api.v1.owner.yard.request.GetYardRequest;
import com.swp.backend.api.v1.owner.yard.updateYardRequest.UpdateYardRequest;
import com.swp.backend.api.v1.owner.yard.request.YardRequest;
import com.swp.backend.api.v1.owner.yard.response.CreateYardSuccessResponse;
import com.swp.backend.api.v1.owner.yard.response.GetYardDetailResponse;
import com.swp.backend.api.v1.owner.yard.response.GetYardResponse;
import com.swp.backend.entity.YardEntity;
import com.swp.backend.exception.ErrorResponse;
import com.swp.backend.exception.InactivateProcessException;
import com.swp.backend.model.MessageResponse;
import com.swp.backend.service.InactivationService;
import com.swp.backend.service.SecurityContextService;
import com.swp.backend.service.YardService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/v1/owners/me")
public class YardRestApi {
    private YardService yardService;
    private SecurityContextService securityContextService;
    private InactivationService inactivationService;
    private Gson gson;

    @PostMapping(value = "yards")
    public ResponseEntity<String> createYard(@RequestParam(name = "yard") String yard, @RequestParam(name = "images") MultipartFile[] images) {
        YardRequest yardRequest;
        try {
            yardRequest = gson.fromJson(yard, YardRequest.class);
        } catch (JsonParseException exception) {
            exception.printStackTrace();
            yardRequest = null;
        }

        if (yardRequest == null) {
            return ResponseEntity.badRequest().body("Missing body");
        }
        try {
            SecurityContext context = SecurityContextHolder.getContext();
            String userId = securityContextService.extractUsernameFromContext(context);
            yardService.createNewYard(userId, yardRequest, images);
            CreateYardSuccessResponse successResponse = CreateYardSuccessResponse.builder().message("Create yard success!").build();
            return ResponseEntity.ok(gson.toJson(successResponse));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping(value = "yards/search")
    public ResponseEntity<String> showAllYard(@RequestBody(required = false) GetYardRequest getYardRequest) {
        try {
            GetYardResponse response;
            SecurityContext securityContext = SecurityContextHolder.getContext();
            String ownerId = securityContextService.extractUsernameFromContext(securityContext);
            if (getYardRequest == null) {
                response = yardService.findAllYardByOwnerId(ownerId, null, null);
            } else {
                response = yardService.findAllYardByOwnerId(ownerId, getYardRequest.getItemsPerPage(), getYardRequest.getPage());
            }
            return ResponseEntity.ok().body(gson.toJson(response));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(value = "yards/search/{yardId}")
    public ResponseEntity<String> showYardById(@PathVariable String yardId) {
        try {
            GetYardDetailResponse response;
            SecurityContext securityContext = SecurityContextHolder.getContext();
            String ownerId = securityContextService.extractUsernameFromContext(securityContext);

            YardEntity yardEntity = yardService.getYardByIdAndNotDeleted(yardId);

            if (yardEntity == null) {
                ErrorResponse error = ErrorResponse.builder().message("The yard is deleted or not existed.").build();
                return ResponseEntity.badRequest().body(gson.toJson(error));
            }

            if (!yardEntity.getOwnerId().equals(ownerId)) {
                ErrorResponse error = ErrorResponse.builder().message("The owner is not author of this yard.").build();
                return ResponseEntity.badRequest().body(gson.toJson(error));
            }

            response = yardService.getYardDetailResponseFromYardId(yardId);

            return ResponseEntity.ok().body(gson.toJson(response));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    @DeleteMapping(value = "yards/{yardId}")
    public ResponseEntity<String> deleteYardById(@PathVariable String yardId)
    {
        try
        {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            String ownerId = securityContextService.extractUsernameFromContext(securityContext);

            inactivationService.deleteYard(ownerId, yardId);
            MessageResponse response = new MessageResponse("Delete yard successfully");
            return ResponseEntity.ok().body(gson.toJson(response));
        }
        catch(InactivateProcessException inactivateProcessException)
        {
            ErrorResponse response = ErrorResponse.builder().message(inactivateProcessException.getFilterMessage()).build();
            return ResponseEntity.badRequest().body(gson.toJson(response));
        }
    }
    @PutMapping(value = "yards/{yardId}")
    public ResponseEntity<String> updateyardById(@RequestParam(name = "yard") String yard, @RequestParam(name = "images", required = false) MultipartFile[] images)
    {
        UpdateYardRequest request;
        try {
            request = gson.fromJson(yard, UpdateYardRequest.class);
        } catch (JsonParseException exception) {
            return ResponseEntity.badRequest().body("Cannot parse the request.");
        }

        try {
            SecurityContext context = SecurityContextHolder.getContext();
            String userId = securityContextService.extractUsernameFromContext(context);

            yardService.updateYard(userId, request, images);
            MessageResponse response = new MessageResponse("Update successfully");
            return ResponseEntity.ok(gson.toJson(response));
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }
}
