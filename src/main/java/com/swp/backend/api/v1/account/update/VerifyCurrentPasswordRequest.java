package com.swp.backend.api.v1.account.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCurrentPasswordRequest {
    private String password;
}
