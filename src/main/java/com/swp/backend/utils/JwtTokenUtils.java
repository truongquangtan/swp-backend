package com.swp.backend.utils;

import com.swp.backend.security.SecurityUserDetails;
import io.jsonwebtoken.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;
@Component
public class JwtTokenUtils {
    private final String secretKey = "SWP391";

    @Bean
    public JwtTokenUtils getJwtTokenUtil(){
        return  new JwtTokenUtils();
    }

    public Claims deCodeToken(String token) throws SignatureException, MalformedJwtException, ExpiredJwtException, UnsupportedJwtException, IllegalArgumentException{
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    //Generate token via UserId to identity user and role of user.
    public String doGenerateToken(SecurityUserDetails account) {
        return doGenerateToken(account.getUsername(), account.getRole());
    }

    public String doGenerateToken(String userName, String role) {
        Timestamp createAt = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);
        Timestamp expirationAt = DateHelper.plusMinutes(createAt, 120);

        return Jwts.builder()
                .setSubject(userName)
                .claim("role", role)
                .setIssuedAt(createAt)
                .setExpiration(expirationAt)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }
}
