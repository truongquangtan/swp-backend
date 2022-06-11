package com.swp.backend.api.v1.admin.reactive_account;

import com.google.gson.Gson;
import com.swp.backend.exception.ErrorResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "api/v1/admin")
@RestController
@AllArgsConstructor
public class ReactiveAccount {
    private Gson gson;

    @PostMapping(value = "re-activate-account")
    public ResponseEntity<String> reactiveAccount(@RequestBody(required = false) ReactiveAccount reactiveAccount){
        if(reactiveAccount == null){
            ErrorResponse errorResponse = ErrorResponse.builder().message("Missing body").build();
            return ResponseEntity.ok().body(gson.toJson(errorResponse));
        }
        return ResponseEntity.ok().build();
    }
}
