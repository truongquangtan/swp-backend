package com.swp.backend.api.v1.admin.deactivate_account;

import com.google.gson.Gson;
import com.swp.backend.exception.ErrorResponse;
import com.swp.backend.service.AccountService;
import com.swp.backend.service.SecurityContextService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1/admin")
@AllArgsConstructor
public class DeactivateAccountRestApi {
    private final Gson gson;
    private SecurityContextService securityContextService;
    private AccountService accountService;

    @PutMapping(value = "deactivate-account")
    public ResponseEntity<String> reactiveAccount(@RequestBody(required = false) DeactivateAccountRequest deactivateAccount){
        try {
            if(deactivateAccount == null){
                ErrorResponse errorResponse = ErrorResponse.builder().message("Missing body").build();
                return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
            }

            SecurityContext context = SecurityContextHolder.getContext();
            String requestUserId = securityContextService.extractUsernameFromContext(context);
            if(requestUserId.equals(deactivateAccount.getUserId())){
                ErrorResponse errorResponse = ErrorResponse.builder().message("Can't disable account itself.").build();
                return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
            }
            if(accountService.deactivateAccount(deactivateAccount.getUserId())){
                return ResponseEntity.ok().body("Disable account success!");
            }else {
                ErrorResponse errorResponse = ErrorResponse.builder().message("Server busy disable account failed.").build();
                return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
            }
        }catch (Exception exception){
            ErrorResponse errorResponse = ErrorResponse.builder().message("Server busy can't handle this request.").build();
            return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
        }
    }
}
