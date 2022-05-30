package com.swp.backend.api.v1.uploadavatar;

import com.google.gson.Gson;
import com.swp.backend.entity.UserEntity;
import com.swp.backend.exception.ErrorResponse;
import com.swp.backend.service.FirebaseStoreService;
import com.swp.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(value = "api/v1")
public class UploadAvatarRestApi {

    FirebaseStoreService firebaseStoreService;
    UserService userService;
    Gson gson;

    public UploadAvatarRestApi(FirebaseStoreService firebaseStoreService, UserService userService, Gson gson) {
        this.firebaseStoreService = firebaseStoreService;
        this.userService = userService;
        this.gson = gson;
    }

    @PostMapping("upload-avatar")
    @Operation(description = "Update avatar.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "500", description = "Save avatar failed."),
                    @ApiResponse(responseCode = "200", description = "Return url img.")
            }
    )
    public ResponseEntity<String> uploadAvatar(@RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        //Case request missing file
        if(file == null){
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .error("error-000")
                    .message("Missing file.")
                    .details("Can't get attribute 'file' MultipartFile from request, request must body type form-data.")
                    .build();
            return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
        }
        try {
            //Determine current user call api from spring-security context
            Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if(userDetails instanceof UserDetails){
                String userId = ((UserDetails) userDetails).getUsername();
                //Get details of user
                UserEntity userEntity = userService.findUserByUsername(userId);
                //Case user notfound
                if (userEntity == null){
                    ErrorResponse errorResponse = ErrorResponse.builder()
                            .error("error-001")
                            .message("User not found.")
                            .details("Account not exist, can't query user on database.")
                            .build();
                    return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
                }
                //Call FirebaseStoreService's upload file method and receive url store on firebase store
                String url = firebaseStoreService.uploadFile(file);
                //Set avatar and update on database
                userEntity.setAvatar(url);
                userService.updateUser(userEntity);
                return ResponseEntity.ok().body(url);
            }
        }catch (DataAccessException dataAccessException){
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .error("error-002")
                    .message("Update info failed.")
                    .details("Access database failed. " + dataAccessException.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
        }catch (Exception exception){
            exception.printStackTrace();
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .error("error-003")
                    .message("Exception unpredictable.")
                    .details(exception.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("error-004")
                .message("Exception unpredictable.")
                .details("Undefined")
                .build();
        return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
    }

}
