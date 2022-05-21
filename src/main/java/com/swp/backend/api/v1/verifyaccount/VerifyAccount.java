package com.swp.backend.api.v1.verifyaccount;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1")
public class VerifyAccount {
    @PostMapping(value = "verify-account")
    public ResponseEntity<String> verifyAccount(){
        return ResponseEntity.ok().build();
    }
}
