package com.swp.backend.repository;

import com.swp.backend.entity.LoginStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginStateRepository extends JpaRepository<LoginStateEntity, Integer> {
    public LoginStateEntity findLoginStateByUserId(String userId);
}
