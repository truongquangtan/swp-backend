package com.swp.backend.api.v1.account.forgot;

import com.swp.backend.utils.RegexHelper;
import lombok.Data;

@Data
public class VerifyOtpRequest {
    String email;
    String otpCode;

    public boolean isValid(){
        if(email == null || otpCode == null){
            return false;
        }
        return email.matches(RegexHelper.EMAIL_REGEX) && otpCode.matches(RegexHelper.OTP_REGEX);
    }
}
