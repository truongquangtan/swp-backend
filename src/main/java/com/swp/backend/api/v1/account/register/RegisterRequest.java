package com.swp.backend.api.v1.account.register;

import com.swp.backend.utils.RegexHelper;
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
    public boolean isValidRequest() {
        if (email == null || fullName == null || password == null) {
            return false;
        }
        if (email.trim().length() <= 0 && fullName.trim().length() <= 0 && password.trim().length() <= 0) {
            return false;
        }
        return email.matches(RegexHelper.EMAIL_REGEX);
    }
}
