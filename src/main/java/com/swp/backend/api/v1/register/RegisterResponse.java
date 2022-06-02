package com.swp.backend.api.v1.register;

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
    private boolean isConfirmed;
    private String token;
}
