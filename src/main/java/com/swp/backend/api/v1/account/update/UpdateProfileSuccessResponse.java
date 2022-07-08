package com.swp.backend.api.v1.account.update;


import com.swp.backend.api.v1.account.login.LoginResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class UpdateProfileSuccessResponse extends LoginResponse {
    private String message;
}
