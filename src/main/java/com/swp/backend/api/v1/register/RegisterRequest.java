package com.swp.backend.api.v1.register;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String email;
    private String fullName;
    private String password;

    public boolean isValidRequest(){
        if(email == null || fullName == null || password == null){
            return false;
        }
        return email.trim().length() > 0 && fullName.trim().length() > 0 && password.trim().length() > 0 && email.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    }
}
