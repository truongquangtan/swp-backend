package com.swp.backend.api.v1.admin.reactive_account;

import com.google.gson.Gson;
import com.swp.backend.exception.ErrorResponse;
import com.swp.backend.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "api/v1/admin")
@RestController
@AllArgsConstructor
public class ReactiveAccount {
    private Gson gson;
    private AccountService accountService;

    @PutMapping(value = "reactivate-account")
    public ResponseEntity<String> reactiveAccount(@RequestBody(required = false) ReactiveAccountRequest reactiveAccount) {
        try {
            if (reactiveAccount == null) {
                ErrorResponse errorResponse = ErrorResponse.builder().message("Missing body").build();
                return ResponseEntity.ok().body(gson.toJson(errorResponse));
            }
            if (accountService.reactivateAccount(reactiveAccount.getUserId())) {
                return ResponseEntity.ok().body("Reactivate account success!");
            } else {
                ErrorResponse errorResponse = ErrorResponse.builder().message("Reactive success.").build();
                return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
            }
        } catch (DataAccessException dataAccessException) {
            ErrorResponse errorResponse = ErrorResponse.builder().message("Server busy can't handle this request.").build();
            return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
        }
    }
}
