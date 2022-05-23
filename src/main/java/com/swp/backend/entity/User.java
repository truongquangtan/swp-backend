package com.swp.backend.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @Column(name = "id")
    private String userId;
    @Column(name = "email")
    private String email;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "phone")
    private String phone;
    @Column(name = "password")
    private String password;
    @Column(name = "avatar")
    private String avatar;
    @Column(name = "otp_code")
    private String optCode;
    @Column(name = "otp_expire")
    private Timestamp otpExpire;
    @Column(name = "create_at")
    private Timestamp createAt;
    @Column(name = "is_confirmed")
    private boolean isConfirmed;
    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;
    @Column(name = "role")
    private String role;
}
