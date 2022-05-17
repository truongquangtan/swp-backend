package com.swp.backend.repository;

import com.swp.backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    public UserEntity findUserEntityByEmail(String email);
    public UserEntity findUserEntityByUserId(int userId);
    public UserEntity findUserEntityByPhoneOrUserId(String email, int userId);
}
