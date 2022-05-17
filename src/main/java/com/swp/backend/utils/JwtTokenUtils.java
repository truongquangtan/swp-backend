package com.swp.backend.utils;

import com.swp.backend.entity.UserEntity;
import io.jsonwebtoken.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Date;
@Component
public class JwtTokenUtils {
    private final String secretKey = "SWP391";
    private final int distanceExpiration = 60 * 60 * 1000;

    @Bean
    public JwtTokenUtils getJwtTokenUtil(){
        return  new JwtTokenUtils();
    }

    public Claims deCodeToken(String token) throws SignatureException, MalformedJwtException, ExpiredJwtException, UnsupportedJwtException, IllegalArgumentException{
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }


    public String doGenerateToken(UserEntity user) {
        Date createAt = new Date();
        Date expirationAt = new Date(createAt.getTime() + distanceExpiration);
        return Jwts.builder()
                .setSubject(String.valueOf(user.getUserId()))
                .claim("role", user.getRole())
                .setIssuedAt(createAt)
                .setExpiration(expirationAt)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }
}
