package com.swp.backend.service;

import com.swp.backend.entity.AccountOtpEntity;
import com.swp.backend.repository.AccountOtpRepository;
import com.swp.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Random;

@Service
@AllArgsConstructor
public class OtpStateService {
    AccountOtpRepository accountOtpRepository;
    EmailService emailService;

    private String generateOtp() {
        return new DecimalFormat("000000").format(new Random().nextInt(999999));
    }

    public AccountOtpEntity generateOtp(String userId){
        String otp = generateOtp();
        Timestamp createAt = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);
        Timestamp expireAt = DateHelper.plusMinutes(createAt, 20);
        AccountOtpEntity accountOtpEntity = accountOtpRepository.findOtpStateByUserId(userId);
        if(accountOtpEntity != null){
            accountOtpEntity.setOtpCode(otp);
            accountOtpEntity.setCreateAt(createAt);
            accountOtpEntity.setExpireAt(expireAt);
        }else {
            accountOtpEntity = AccountOtpEntity.builder().userId(userId).createAt(createAt).expireAt(expireAt).otpCode(otp).build();
        }
        accountOtpRepository.save(accountOtpEntity);
        return accountOtpEntity;
    }

    public AccountOtpEntity findOtpStateByUserId(String userId){
        return accountOtpRepository.findOtpStateByUserId(userId);
    }

    public void sendEmailOtpRestPassword(String userId, String email) throws DataAccessException {
        AccountOtpEntity accountOtp = accountOtpRepository.findOtpStateByUserId(userId);
        if(accountOtp == null){
            accountOtp = AccountOtpEntity.builder().userId(userId).build();
        }
        String otp = generateOtp();
        String emailSubject = "RESET PASSWORD PLAYGROUND BASKETBALL CODE";
        String body = "Reset OTP: " + otp;
        Timestamp createAt = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);
        Timestamp expireAt = DateHelper.plusMinutes(createAt, 20);

        accountOtp.setOtpCode(otp);
        accountOtp.setCreateAt(createAt);
        accountOtp.setExpireAt(expireAt);
        accountOtpRepository.save(accountOtp);
        emailService.sendSimpleMessage(email, emailSubject, body);
    }

    public boolean verifyOtp(String userId, String otp){
        AccountOtpEntity accountOtp = accountOtpRepository.findOtpStateByUserId(userId);
        if(accountOtp == null){
            return false;
        }
        return accountOtp.getOtpCode().matches(otp);
    }
}
