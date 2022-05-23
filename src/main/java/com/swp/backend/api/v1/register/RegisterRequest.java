package com.swp.backend.api.v1.register;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String email;
    private String fullName;
    private String password;
    private String phone;

    //Check valid request form
    public boolean isValidRequest(){
        String regexEmail = "^[_A-Za-z\\d-+]+(\\.[_A-Za-z\\d-]+)*@[A-Za-z\\d-]+(\\.[A-Za-z\\d]+)*(\\.[A-Za-z]{2,})$";
        String regexPhone = "\\d{10}";
        if(email == null || fullName == null || password == null || phone == null){
            return false;
        }
        if(email.trim().length() <= 0 && fullName.trim().length() <= 0 && password.trim().length() <= 0 && phone.trim().length() <= 0){
            return false;
        }
        return email.matches(regexEmail) && phone.matches(regexPhone);
    }
}
