package com.swp.backend.constance;

public class ApiEndpointProperties {
    public static String [] publicEndpoint = {
            "/api/v1/login",
            "/api/v1/register",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/api/v1/filter",
            "/api/v1/districts",
            "/api/v1/districts/**",
            "/api/v1/provinces",
            "/api/v1/provinces/**",
            "/api/v1/forgot/send-mail",
            "/api/v1/forgot/confirm-otp",
            "/api/v1/yards/search",
            "/api/v1/yards",
            "/api/v1/yards/**",
            "/api/v1/slots/get-by-date",
            "/api/v1/sub-yards",
            "/api/v1/sub-yards/**"
    };
    public static String[] ownerOnlyEndpoint = {

    };

    public static String[] adminOnlyEndpoint = {
            "/api/v1/admin/reactivate-account",
            "/api/v1/admin/deactivate-account",
            "/api/v1/admin/owner-register",
            "/api/v1/admin/view-all-user"
    };
}
