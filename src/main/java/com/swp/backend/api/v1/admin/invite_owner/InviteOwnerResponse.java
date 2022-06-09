package com.swp.backend.api.v1.admin.invite_owner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class InviteOwnerResponse {
    private String message;
    private String email;
    private String password;
}
