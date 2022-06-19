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
            "^/api/v1/sub-yards{1}.*",
            "^/api/v1/admin/filter-account{1}"
    };
    public static String[] publicEndpoint = {
            "/api/v1/admin/filter-account",
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
            "/api/v1/owners/**"
    };

    public static String[] adminOnlyEndpoint = {
            "/api/v1/admin/owner-register",
            "/api/v1/admin/view-all-user",
            "/api/v1/admin/all-accounts",
            "/api/v1/admin/filter/all-accounts",
            "/api/v1/filter-accounts",
            "/api/v1/admin/accounts/{accountId}"
    };

    public static String[] userOnlyEndpoint = {
            "/api/v1/yards/{yardId}/booking",
            "/api/v1/me/incoming-matches",
            "/api/v1/me/history-booking",
            "/api/v1/me/bookings/{bookingId}"
    };
}
