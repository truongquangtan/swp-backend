package com.swp.backend.api.v1.account.login;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseException {
    private String message;
}
