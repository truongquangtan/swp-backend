package com.swp.backend.repository;

import com.swp.backend.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, String> {
    public AccountEntity findUserEntityByEmail(String email);
    public AccountEntity findUserEntityByUserId(String userId);

    public AccountEntity findUserEntityByPhone(String phone);
}
