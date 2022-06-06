package com.swp.backend.api.v1.admin;

import com.google.gson.Gson;
import com.swp.backend.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1/admin")
@AllArgsConstructor
public class GetAllUserRestApi {
    private AccountService accountService;
    private Gson gson;

    @GetMapping("view-all-user")
    public ResponseEntity<String> getAllUserHasRoleUserOrOwner(){
        return ResponseEntity.ok().body(gson.toJson(accountService.getAllUserHasRoleUser()));
    }
}
