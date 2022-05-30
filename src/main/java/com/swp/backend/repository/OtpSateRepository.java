package com.swp.backend.repository;

import com.swp.backend.entity.OtpState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpSateRepository extends JpaRepository<OtpState, Integer> {
    public OtpState findOtpStateByUserId(String userId);
}
