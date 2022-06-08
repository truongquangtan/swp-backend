package com.swp.backend.api.v1.owner.invite_admin;

import com.swp.backend.api.v1.account.register.RegisterResponse;
import com.swp.backend.entity.AccountEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InviteAdminResponse {
    private String message;
    private String email;
    private String password;
}
