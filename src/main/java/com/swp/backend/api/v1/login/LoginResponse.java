package com.swp.backend.api.v1.login;

import com.swp.backend.model.JwtToken;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String userId;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private boolean isConfirmed;
    private String avatar;
    private JwtToken token;
}
