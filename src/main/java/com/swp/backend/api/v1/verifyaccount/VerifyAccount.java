package com.swp.backend.api.v1.verifyaccount;

import com.google.gson.Gson;
import com.swp.backend.entity.User;
import com.swp.backend.exception.ErrorResponse;
import com.swp.backend.service.UserService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.Date;

@RestController
@RequestMapping(value = "api/v1")
public class VerifyAccount {
    UserService userService;
    Gson gson;

    public VerifyAccount(UserService userService, Gson gson) {
        this.userService = userService;
        this.gson = gson;
    }

    @PostMapping(value = "verify-account")
    public ResponseEntity<String> verifyAccount(@RequestBody(required = false) RequestVerify verify){
        if(verify == null || verify.getOtpCode().length() <= 0){
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .error("auth-016")
                    .message("Bad body")
                    .details("Missing body or can't otp code not valid.")
                    .build();
            return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
        }
        Object userDetail = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!(userDetail instanceof UserDetails)){
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .error("auth-017")
                    .message("Server error.")
                    .details("Server temp handle this request.")
                    .build();
            return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
        }
        try {
            String userId = ((UserDetails) userDetail).getUsername();
            User user = userService.findUserByUsername(userId);
            if (user == null){
                ErrorResponse errorResponse = ErrorResponse.builder()
                        .error("auth-017")
                        .message("User notfound")
                        .details("User may be deleted.")
                        .build();
                return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
            }
            Timestamp now = Timestamp.from(new Date().toInstant());
            if(now.after(user.getOtpExpire())){
                ErrorResponse errorResponse = ErrorResponse.builder()
                        .error("auth-018")
                        .message("Otp expire")
                        .details("Time otp expire: " + user.getOtpExpire())
                        .build();
                return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
            }

            if(user.getOptCode().matches(verify.getOtpCode())){
                user.setConfirmed(true);
                userService.updateUser(user);
                return ResponseEntity.ok().body("Verify account success!");
            }else {
                ErrorResponse errorResponse = ErrorResponse.builder()
                        .error("auth-019")
                        .message("Otp not match")
                        .details("Otp incorrect or not lasted otp.")
                        .build();
                return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
            }
        }catch (DataAccessException dataAccessException){
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .error("auth-018")
                    .message("Server access database error.")
                    .details("Server temp handle this request.")
                    .build();
            return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
        }
    }
}
