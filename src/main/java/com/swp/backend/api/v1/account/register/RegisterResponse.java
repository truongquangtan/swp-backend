package com.swp.backend.api.v1.account.register;

import lombok.*;
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
