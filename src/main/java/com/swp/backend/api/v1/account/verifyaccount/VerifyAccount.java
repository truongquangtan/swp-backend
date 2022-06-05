package com.swp.backend.api.v1.account.verifyaccount;

import com.google.gson.Gson;
import com.swp.backend.entity.AccountEntity;
import com.swp.backend.entity.AccountOtpEntity;
import com.swp.backend.service.AccountService;
import com.swp.backend.service.OtpStateService;
import com.swp.backend.service.SecurityContextService;
import com.swp.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;

@RestController
@RequestMapping(value = "api/v1")
@AllArgsConstructor
public class VerifyAccount {
    AccountService accountService;
    SecurityContextService securityContextService;
    Gson gson;
    OtpStateService otpStateService;

    @PostMapping(value = "verify-account")
    public ResponseEntity<String> verifyAccount(@RequestBody(required = false) RequestVerify verify){
        if(verify == null || verify.getOtpCode().length() <= 0){
            return ResponseEntity.badRequest().body("Missing body or otp code not valid.");
        }
        try {
            SecurityContext context = SecurityContextHolder.getContext();
            String userId = securityContextService.extractUsernameFromContext(context);

            AccountOtpEntity accountOtpEntity = otpStateService.findOtpStateByUserId(userId);
            if (accountOtpEntity == null){
                return ResponseEntity.badRequest().body("Otp not available.");
            }

            Timestamp now = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);
            if(now.after(accountOtpEntity.getExpireAt())){
                return ResponseEntity.badRequest().body("Time otp expire: " + accountOtpEntity.getExpireAt());
            }

            if(!accountOtpEntity.getOtpCode().matches(verify.getOtpCode())){
                return ResponseEntity.badRequest().body("Otp incorrect or not lasted otp.");
            }

            AccountEntity accountEntity = accountService.findAccountByUsername(userId);
            if(accountEntity == null){
                return ResponseEntity.badRequest().body("Confirm failed!");
            }

            if(!accountEntity.isConfirmed()){
                accountEntity.setConfirmed(true);
                accountService.updateUser(accountEntity);
            }
            return ResponseEntity.ok().body("Verify account success!");
        }catch (DataAccessException dataAccessException){
            return ResponseEntity.internalServerError().body(dataAccessException.getMessage());
        }
    }

    @GetMapping(value = "verify-account")
    public ResponseEntity<String> resendEmailVerifyAccount(){
        try {
            SecurityContext context = SecurityContextHolder.getContext();
            String userId = securityContextService.extractUsernameFromContext(context);
            AccountEntity accountEntity = accountService.findAccountByUsername(userId);
            if(accountEntity == null){
                return ResponseEntity.badRequest().body("User not exist!");
            }
            AccountOtpEntity accountOtpEntity = otpStateService.generateOtp(userId);
            accountService.sendOtpVerifyAccount(accountEntity, accountOtpEntity);
            return ResponseEntity.ok().body("Resend verify success!");
        }catch (ClassCastException | DataAccessException e){
            return ResponseEntity.internalServerError().body("Server temp can't handle this request." + e.getMessage());
        }
    }
}
