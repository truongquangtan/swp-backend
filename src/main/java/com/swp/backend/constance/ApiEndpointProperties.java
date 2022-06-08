package com.swp.backend.constance;

public class ApiEndpointProperties {
    public static String [] publicEndpoint = {
            "/api/v1/login",
            "/api/v1/register",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/api/v1/filter",
            "/api/v1/district",
            "/api/v1/district/**",
            "/api/v1/province",
            "/api/v1/province/**",
            "/api/v1/forgot/send-mail",
            "/api/v1/forgot/confirm-otp",
            "/api/v1/yard/search",
            "/api/v1/admin/view-all-user",
            "/api/v1/sub-yard/get-by-big-yard"
    };
}
