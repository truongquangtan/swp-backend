package com.swp.backend.api.v1.register;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String phone;
    private String email;
    private String password;
}
