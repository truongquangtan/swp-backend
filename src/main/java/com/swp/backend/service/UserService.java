package com.swp.backend.service;

import com.swp.backend.entity.User;
import com.swp.backend.repository.UserRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public User findUserByUsername(String username){
        //Case username is null
        if(username == null){
            return null;
        }
        //Find user by username, phone, or password
        if(username.matches("^[_A-Za-z\\d-+]+(\\.[_A-Za-z\\d-]+)*@[A-Za-z\\d-]+(\\.[A-Za-z\\d]+)*(\\.[A-Za-z]{2,})$")){
            return userRepository.findUserEntityByEmail(username);
        } else if (username.matches("\\d+")){
            return  userRepository.findUserEntityByPhone(username);
        }else {
            return userRepository.findUserEntityByUserId(username);
        }
    }

    public User createUser(String email, String fullName, String password, String phone, String role) throws DataAccessException{
        String otp = new DecimalFormat("000000").format(new Random().nextInt(999999));
        int distanceExpireOtp = 5 * 60 * 1000;
        String uuid = UUID.randomUUID().toString();
        User user = User.builder()
            .userId(uuid)
            .email(email)
            .fullName(fullName)
            .optCode(otp)
            .phone(phone)
            .password(passwordEncoder.encode(password))
            .otpExpire(new Timestamp(System.currentTimeMillis() + distanceExpireOtp))
            .role(role)
            .createAt(new Timestamp(System.currentTimeMillis()))
        .build();
        userRepository.save(user);
        return user;
    }
    public void sendOtpVerifyAccount(User user){
        String emailSubject = "VERIFY ACCOUNT";
        String emailBody = "Your code verify account: " + user.getOptCode();
        emailService.sendSimpleMessage(user.getEmail(), emailSubject, emailBody);
    }

    public void updateUser(User user) throws DataAccessException{
         userRepository.save(user);
    }
}
