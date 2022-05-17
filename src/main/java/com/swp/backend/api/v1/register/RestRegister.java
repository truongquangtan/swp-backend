package com.swp.backend.api.v1.register;

import com.google.gson.Gson;
import com.swp.backend.entity.UserEntity;
import com.swp.backend.service.EmailService;
import com.swp.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestRegister {
    Gson gson;
    UserService userService;
    EmailService emailService;

    public RestRegister(Gson gson, UserService userService, EmailService emailService) {
        this.gson = gson;
        this.userService = userService;
        this.emailService = emailService;
    }

    @PostMapping("api/v1/register")
    public ResponseEntity<String> register(@RequestBody String body){
        RegisterRequest userRegister = gson.fromJson(body, RegisterRequest.class);
        UserEntity newUser = userService.createUser(userRegister.getEmail(), userRegister.getPassword());
        if(newUser == null){
            return ResponseEntity.badRequest().body("Register user failed!");
        }
        String emailSubject = "Verify account";
        String emailBody = "Your code verify account: " + newUser.getOptCode();
//        emailService.sendSimpleMessage(newUser.getEmail(), emailSubject, emailBody);
        return ResponseEntity.ok().body("OK");
    }

}
