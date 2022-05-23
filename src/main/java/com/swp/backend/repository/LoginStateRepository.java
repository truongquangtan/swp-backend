package com.swp.backend.repository;

import com.swp.backend.entity.LoginState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginStateRepository extends JpaRepository<LoginState, Integer> {
    public LoginState findLoginStateByUserId(String userId);
}
