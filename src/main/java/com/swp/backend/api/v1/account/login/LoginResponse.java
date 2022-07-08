package com.swp.backend.api.v1.account.login;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
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
