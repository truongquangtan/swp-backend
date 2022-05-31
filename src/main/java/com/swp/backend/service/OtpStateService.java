package com.swp.backend.service;

import com.swp.backend.entity.OtpStateEntity;
import com.swp.backend.repository.OtpSateRepository;
import com.swp.backend.utils.DateHelper;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Random;

@Service
public class OtpStateService {
    OtpSateRepository otpSateRepository;

    public OtpStateService(OtpSateRepository otpSateRepository) {
        this.otpSateRepository = otpSateRepository;
    }

    private String generateOtp() {
        return new DecimalFormat("000000").format(new Random().nextInt(999999));
    }

    public OtpStateEntity generateOtp(String userId){
        String otp = generateOtp();
        Timestamp createAt = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);
        Timestamp expireAt = DateHelper.plusMinutes(createAt, 20);
        OtpStateEntity otpStateEntity = otpSateRepository.findOtpStateByUserId(userId);
        if(otpStateEntity != null){
            otpStateEntity.setOtpCode(otp);
            otpStateEntity.setCreateAt(createAt);
            otpStateEntity.setExpireAt(expireAt);
        }else {
            otpStateEntity = OtpStateEntity.builder().userId(userId).createAt(createAt).expireAt(expireAt).otpCode(otp).build();
        }
        otpSateRepository.save(otpStateEntity);
        return otpStateEntity;
    }

    public OtpStateEntity findOtpStateByUserId(String userId){
        return otpSateRepository.findOtpStateByUserId(userId);
    }
}
