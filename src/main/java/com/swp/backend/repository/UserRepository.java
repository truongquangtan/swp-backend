package com.swp.backend.repository;

import com.swp.backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    public UserEntity findUserEntityByEmail(String email);
    public UserEntity findUserEntityByUserId(String userId);

    public UserEntity findUserEntityByPhone(String phone);
}
