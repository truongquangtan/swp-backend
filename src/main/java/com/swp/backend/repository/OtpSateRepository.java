package com.swp.backend.repository;

import com.swp.backend.entity.OtpStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpSateRepository extends JpaRepository<OtpStateEntity, Integer> {
    public OtpStateEntity findOtpStateByUserId(String userId);
}
