package com.swp.backend.api.v1.account.forgot;

import lombok.Data;

@Data
public class SendMailRequest {
    private String email;
}
