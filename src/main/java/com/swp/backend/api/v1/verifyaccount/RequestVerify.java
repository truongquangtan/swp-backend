package com.swp.backend.api.v1.verifyaccount;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestVerify {
    private String otpCode;
}
