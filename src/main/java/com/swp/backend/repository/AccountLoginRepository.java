package com.swp.backend.repository;

import com.swp.backend.entity.AccountLoginEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountLoginRepository extends JpaRepository<AccountLoginEntity, Integer> {
    public List<AccountLoginEntity> findLoginStateByUserId(String userId);

    public AccountLoginEntity findTopByAccessToken(String accessToken);
}
