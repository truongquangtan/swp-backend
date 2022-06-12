package com.swp.backend.repository;

import com.swp.backend.entity.AccountOtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountOtpRepository extends JpaRepository<AccountOtpEntity, Integer> {
    AccountOtpEntity findOtpStateByUserId(String userId);
}
