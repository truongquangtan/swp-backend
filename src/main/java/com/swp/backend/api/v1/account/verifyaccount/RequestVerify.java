package com.swp.backend.api.v1.account.verifyaccount;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestVerify {
    private String otpCode;
}
