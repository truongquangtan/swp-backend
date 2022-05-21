package com.swp.backend.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "login_state")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "access_token")
    private String accessToken;
    @Column(name = "is_logout")
    private boolean isLogout = false;
}
