package com.swp.backend.constance;

public class ApiEndpointProperties {
    public static String[] nonFilterEndpoint = {
            "^/api/v1/login{1}",
            "^/api/v1/register{1}",
            "^/v3/api-docs{1}.*",
            "^/swagger-ui{1}.*",
            "^/api/v1/districts{1}.*",
            "^/api/v1/provinces{1}.*",
            "^/api/v1/forgot/send-mail{1}",
            "^/api/v1/forgot/confirm-otp{1}",
            "^/api/v1/yards/search{1}",
            "^/api/v1/yards{1}",
            "^/api/v1/yards/{1}[^/]*",
            "^/api/v1/slots/get-by-date{1}",
            "^/api/v1/sub-yards{1}.*"
    };
    public static String[] publicEndpoint = {
            "/api/v1/login",
            "/api/v1/register",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/api/v1/districts/**",
            "/api/v1/provinces/**",
            "/api/v1/forgot/send-mail",
            "/api/v1/forgot/confirm-otp",
            "/api/v1/yards/search",
            "/api/v1/yards",
            "/api/v1/yards/{yardId}",
            "/api/v1/slots/get-by-date",
            "/api/v1/sub-yards",
            "/api/v1/sub-yards/**"
    };
    public static String[] ownerOnlyEndpoint = {
        "/api/v1/owner/yard/add-yard"
    };

    public static String[] adminOnlyEndpoint = {
            "/api/v1/admin/reactivate-account",
            "/api/v1/admin/deactivate-account",
            "/api/v1/admin/owner-register",
            "/api/v1/admin/view-all-user"
    };

    public static String[] userOnlyEndpoint = {
            "/api/v1/yards/{yardId}/booking",
            "/api/v1/me/incoming-matches",
            "/api/v1/me/history-booking"
    };
}
