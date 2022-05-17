package com.swp.backend.api.v1.login;

import com.google.gson.Gson;
import com.swp.backend.entity.UserEntity;
import com.swp.backend.service.UserService;
import com.swp.backend.utils.JwtTokenUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestLogin {
    Gson gson;
    UserService userService;
    BCryptPasswordEncoder bCryptPasswordEncoder;
    JwtTokenUtils jwtTokenUtils;

    public RestLogin(Gson gson, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder, JwtTokenUtils jwtTokenUtils) {
        this.gson = gson;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @PostMapping("api/v1/login")
    public ResponseEntity<String> login(@RequestBody String body ){
        if(body == null){
            return ResponseEntity.badRequest().body("Can't determined username and password from request. Try sent json data");
        }
        LoginRequest loginRequest = gson.fromJson(body, LoginRequest.class);
        UserEntity loginUser = userService.findUserByUsername(loginRequest.getUsername());
        if(loginUser == null){
            return ResponseEntity.badRequest().body("Username not exist.");
        }
        if(bCryptPasswordEncoder.matches(loginRequest.getPassword(), loginUser.getPassword())){
            String accessToken = jwtTokenUtils.doGenerateToken(loginUser);
            return ResponseEntity.ok().body(accessToken);
        }
        return ResponseEntity.badRequest().build();
    }
}
