package com.swp.backend.api.v1.account.forgot;

import lombok.Data;

@Data
public class VerifyOtpRequest {
    String email;
    String otpCode;
}
