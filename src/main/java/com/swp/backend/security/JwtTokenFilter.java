package com.swp.backend.security;

import com.google.gson.Gson;
import com.swp.backend.constance.ApiEndpointProperties;
import com.swp.backend.entity.AccountLoginEntity;
import com.swp.backend.exception.ErrorResponse;
import com.swp.backend.service.AccountLoginService;
import com.swp.backend.utils.JwtTokenUtils;
import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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
@AllArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    JwtTokenUtils jwtTokenUtils;
    AccountLoginService accountLoginService;
    Gson gson;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        boolean isPublicApi = Arrays.stream(ApiEndpointProperties.publicEndpoint).anyMatch(url -> request.getRequestURI().startsWith(url));
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
            AccountLoginEntity login = accountLoginService.findLogin(claims.getSubject());
            if(login == null){
                sendErrorResponse(response, 400, "Token not available.");
                return;
            }

            if(!login.getAccessToken().matches(token)){
                sendErrorResponse(response, 400, "Token does not match the latest token.");
                return;
            }

            if(login.isLogout()){
                sendErrorResponse(response, 400, "User logged out.");
                return;
            }

            SecurityUserDetails securityUserDetails = SecurityUserDetails.builder()
                    .username(claims.getSubject())
                    .role((String) claims.get("role"))
                    .password(UUID.randomUUID().toString())
                    .build();

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(securityUserDetails, null, securityUserDetails.getAuthorities());
            authenticationToken.setDetails(token);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            sendErrorResponse(response, 400,  "Token invalid.");
            return;
        }catch (Exception exception){
            sendErrorResponse(response, 500,  "Server temp error.");
            return;
        }
        chain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(message)
                .build();
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(gson.toJson(errorResponse));
        out.flush();
    }
}
