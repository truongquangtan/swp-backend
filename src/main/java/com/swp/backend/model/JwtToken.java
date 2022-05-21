package com.swp.backend.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
public class JwtToken {
    private Date createAt;
    private Date expiredAt;
    private String token;

    public JwtToken(Date createAt, Date expiredAt, String token) {
        this.createAt = createAt;
        this.expiredAt = expiredAt;
        this.token = token;
    }
}
