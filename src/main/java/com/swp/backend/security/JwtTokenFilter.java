package com.swp.backend.security;

import com.google.gson.Gson;
import com.swp.backend.constance.ApiEndpointProperties;
import com.swp.backend.entity.LoginState;
import com.swp.backend.entity.User;
import com.swp.backend.exception.ErrorResponse;
import com.swp.backend.service.LoginStateService;
import com.swp.backend.service.UserService;
import com.swp.backend.utils.JwtTokenUtils;
import io.jsonwebtoken.*;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.UUID;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    JwtTokenUtils jwtTokenUtils;
    UserService userService;
    LoginStateService loginStateService;
    Gson gson;

    public JwtTokenFilter(JwtTokenUtils jwtTokenUtils, UserService userService, LoginStateService loginStateService, Gson gson) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.userService = userService;
        this.loginStateService = loginStateService;
        this.gson = gson;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        boolean isPublicApi = Arrays.stream(ApiEndpointProperties.publicEndpoint).anyMatch(url -> url.startsWith(request.getRequestURI()));
        if(isPublicApi){
            chain.doFilter(request, response);
            return;
        }
        // Get authorization header and validate
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // Get jwt token and validate
        final String token = header.split(" ")[1].trim();
        try {
            Claims claims = jwtTokenUtils.deCodeToken(token);
            LoginState login = loginStateService.findLogin(claims.getSubject());
            if(login == null){
                sendErrorResponse(response, 400, "auth-001", "Token not available.", "Can't find info user login in Database.");
                return;
            }

            if(!login.getAccessToken().matches(token)){
                sendErrorResponse(response, 400, "auth-002", "Token does not match the latest token.", "Try login to get latest token.");
                return;
            }

            if(login.isLogout()){
                sendErrorResponse(response, 400, "auth-003", "User logged out.", "Token has been delete.");
                return;
            }

            User user = userService.findUserByUsername(claims.getSubject());
            if(user != null){
                UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(String.valueOf(user.getUserId()))
                        .password(UUID.randomUUID().toString())
                        .roles(user.getRole())
                        .build();
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }else {
                sendErrorResponse(response, 400, "auth-004", "User is not exist.", "User may be delete by admin.");
                return;
            }
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            sendErrorResponse(response, 400, "auth-005", "Token invalid.", e.getMessage());
            return;
        }
        chain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String error, String message, String details) throws IOException {
        response.setStatus(status);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(error)
                .message(message)
                .details(details)
                .build();
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(gson.toJson(errorResponse));
        out.flush();
    }
}
