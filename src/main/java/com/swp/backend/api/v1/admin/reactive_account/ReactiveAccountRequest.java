package com.swp.backend.api.v1.admin.reactive_account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReactiveAccountRequest {
    private String userId;
}
