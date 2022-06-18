package com.swp.backend.repository;

import com.swp.backend.entity.AccountLoginEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountLoginRepository extends JpaRepository<AccountLoginEntity, Integer> {
    public AccountLoginEntity findLoginStateByUserId(String userId);
    public AccountLoginEntity findAccountLoginEntityByAccessToken(String accessToken);
}
