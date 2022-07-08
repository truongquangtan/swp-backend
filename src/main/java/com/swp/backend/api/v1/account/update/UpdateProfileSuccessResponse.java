package com.swp.backend.api.v1.account.update;


import com.swp.backend.api.v1.account.login.LoginResponse;
import lombok.*;

@Getter
@Setter
public class UpdateProfileSuccessResponse extends LoginResponse {
    private String message;

    @Builder
    public UpdateProfileSuccessResponse(String userId, String avatar, String role, String fullName, String email, boolean isConfirm, String phone, String token, String message) {
        super(userId, avatar, role, fullName, email, isConfirm, phone, token);
        this.message = message;
    }
}
