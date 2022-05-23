package com.swp.backend.api.v1.login;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    private String username;
    private String password;

    //Checking request is valid formant require
    public boolean isValidRequest(){
        if(username == null || password == null){
            return false;
        }
        return username.trim().length() > 0 && password.trim().length() > 0;
    }
}
