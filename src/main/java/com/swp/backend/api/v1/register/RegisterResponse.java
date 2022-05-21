package com.swp.backend.api.v1.register;

import com.swp.backend.model.JwtToken;
import lombok.*;

import java.sql.Timestamp;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {
    private String userId;
    private String fullName;
    private String email;
    private String role;
    private Timestamp createAt;
    private boolean isConfirmed;
    private JwtToken token;
}
