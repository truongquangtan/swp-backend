package com.swp.backend.repository;

import com.swp.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    public User findUserEntityByEmail(String email);
    public User findUserEntityByUserId(String userId);

    public User findUserEntityByPhone(String phone);
}
