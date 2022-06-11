package com.swp.backend.api.v1.account.login;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String userId;
    private String avatar;
    private String role;
    private String fullName;
    private String email;
    private boolean isConfirm;
    private String phone;
    private String token;
}
