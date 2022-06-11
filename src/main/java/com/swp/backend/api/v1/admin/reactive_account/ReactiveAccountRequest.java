package com.swp.backend.api.v1.admin.reactive_account;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReactiveAccountRequest {
    private String userId;
}
