package com.swp.backend.api.v1.admin.deactivate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeactivateAccountRequest {
    private String userId;
}
