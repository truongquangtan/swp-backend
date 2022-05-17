package com.swp.backend.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int userId;
    @Column(name = "email")
    private String email;
    @Column(name = "phone")
    private String phone;
    @Column(name = "avatar")
    private String avatar;
    @Column(name = "password")
    private String password;
    @Column(name = "create_at")
    private Timestamp createAt = new Timestamp(System.currentTimeMillis());
    @Column(name = "otp_code")
    private String optCode;
    @Column(name = "otp_expire")
    private Timestamp otpExpire;
    @Column(name = "is_confirmed")
    private boolean isConfirmed;
    @Column(name = "is_active")
    private boolean isActive;
    @Column(name = "role")
    private String role = "USER";
}
