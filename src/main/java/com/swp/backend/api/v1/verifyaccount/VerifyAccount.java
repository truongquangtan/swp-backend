package com.swp.backend.api.v1.verifyaccount;

import com.google.gson.Gson;
import com.swp.backend.entity.OtpStateEntity;
import com.swp.backend.entity.UserEntity;
import com.swp.backend.exception.ErrorResponse;
import com.swp.backend.service.OtpStateService;
import com.swp.backend.service.SecurityContextService;
import com.swp.backend.service.UserService;
import com.swp.backend.utils.DateHelper;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;

@RestController
@RequestMapping(value = "api/v1")
public class VerifyAccount {
    UserService userService;
    SecurityContextService securityContextService;
    Gson gson;
    OtpStateService otpStateService;

    public VerifyAccount(UserService userService, SecurityContextService securityContextService, Gson gson, OtpStateService otpStateService) {
        this.userService = userService;
        this.securityContextService = securityContextService;
        this.gson = gson;
        this.otpStateService = otpStateService;
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
        try {
            SecurityContext context = SecurityContextHolder.getContext();
            String userId = securityContextService.extractUsernameFromContext(context);

            OtpStateEntity otpStateEntity = otpStateService.findOtpStateByUserId(userId);
            if (otpStateEntity == null){
                ErrorResponse errorResponse = ErrorResponse.builder()
                        .error("auth-017")
                        .message("Otp not available.")
                        .details("User may be deleted or otp generate failed.")
                        .build();
                return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
            }

            Timestamp now = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);
            if(now.after(otpStateEntity.getExpireAt())){
                ErrorResponse errorResponse = ErrorResponse.builder()
                        .error("auth-018")
                        .message("Otp expire")
                        .details("Time otp expire: " + otpStateEntity.getExpireAt())
                        .build();
                return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
            }

            if(!otpStateEntity.getOtpCode().matches(verify.getOtpCode())){
                ErrorResponse errorResponse = ErrorResponse.builder()
                        .error("auth-020")
                        .message("Otp not match")
                        .details("Otp incorrect or not lasted otp.")
                        .build();
                return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
            }

            UserEntity userEntity = userService.findUserByUsername(userId);
            if(userEntity == null){
                ErrorResponse errorResponse = ErrorResponse.builder()
                        .error("auth-019")
                        .message("User is not found")
                        .details("Account may be deleted.")
                        .build();
                return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
            }

            if(!userEntity.isConfirmed()){
                userEntity.setConfirmed(true);
                userService.updateUser(userEntity);
            }
            return ResponseEntity.ok().body("Verify account success!");
        }catch (DataAccessException dataAccessException){
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .error("auth-021")
                    .message("Server access database error.")
                    .details("Server temp handle this request.")
                    .build();
            return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
        }
    }

    @GetMapping(value = "verify-account")
    public ResponseEntity<String> resendEmailVerifyAccount(){
        try {
            SecurityContext context = SecurityContextHolder.getContext();
            String userId = securityContextService.extractUsernameFromContext(context);
            UserEntity userEntity = userService.findUserByUsername(userId);
            if(userEntity == null){
                ErrorResponse errorResponse = ErrorResponse.builder()
                        .error("auth-017")
                        .message("User notfound")
                        .details("User may be deleted.")
                        .build();
                return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
            }
            OtpStateEntity otpStateEntity = otpStateService.generateOtp(userId);
            userService.sendOtpVerifyAccount(userEntity, otpStateEntity);
            return ResponseEntity.ok().body("Resend verify success!");
        }catch (ClassCastException | DataAccessException e){
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .error("auth-018")
                    .message("Server access database error.")
                    .details("Server temp can't handle this request." + e.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
        }
    }
}
