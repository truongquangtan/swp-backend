package com.swp.backend.api.v1.account.verifyaccount;

import com.swp.backend.utils.RegexHelper;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestVerify {
    private String otpCode;

    public boolean isValid() {
        return otpCode != null && otpCode.matches(RegexHelper.OTP_REGEX);
    }
}
