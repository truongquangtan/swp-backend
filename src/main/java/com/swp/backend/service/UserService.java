package com.swp.backend.service;

import com.swp.backend.entity.UserEntity;
import com.swp.backend.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Random;

@Service
public class UserService {
    private final
    UserRepository userRepository;
    BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity findUserByUsername(String username){
        if(username.matches("^(.+)@(.+)$")){
            return userRepository.findUserEntityByEmail(username);
        }
        if(username.matches("\\d+")){
            return  userRepository.findUserEntityByPhoneOrUserId(username, Integer.parseInt(username));
        }
        return  null;
    }

    public UserEntity createUser(String email, String password){
        String otp = new DecimalFormat("000000").format(new Random().nextInt(999999));
        int distanceExpireOtp = 5 * 60 * 1000;
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setOptCode(otp);
        user.setPassword(passwordEncoder.encode(password));
        user.setOtpExpire(new Timestamp(System.currentTimeMillis() + distanceExpireOtp));
        try {
            userRepository.save(user);
            return user;
        }catch (Exception exception){
            exception.printStackTrace();
            return null;
        }
    }
}
