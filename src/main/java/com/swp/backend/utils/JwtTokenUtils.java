package com.swp.backend.utils;

import com.swp.backend.entity.UserEntity;
import com.swp.backend.model.JwtToken;
import io.jsonwebtoken.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Date;
@Component
public class JwtTokenUtils {
    private final String secretKey = "SWP391";
    private final int distanceExpiration = 120 * 60 * 1000;

    @Bean
    public JwtTokenUtils getJwtTokenUtil(){
        return  new JwtTokenUtils();
    }

    public Claims deCodeToken(String token) throws SignatureException, MalformedJwtException, ExpiredJwtException, UnsupportedJwtException, IllegalArgumentException{
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    //Generate token via UserId to identity user and role of user.
    public JwtToken doGenerateToken(UserEntity userEntity) {
        Date createAt = new Date();
        Date expirationAt = new Date(createAt.getTime() + distanceExpiration);
        String token =
                Jwts.builder()
                .setSubject(String.valueOf(userEntity.getUserId()))
                .claim("role", userEntity.getRole())
                .setIssuedAt(createAt)
                .setExpiration(expirationAt)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
        return new JwtToken(createAt, expirationAt, token);
    }
}
