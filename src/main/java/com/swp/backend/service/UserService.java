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
        String emailSubject = "VERIFY PLAYGROUND BASKETBALL CODE";
        String htmlBody =
                "<img style=\"display: block; width: 60px; padding: 2px; height: 60px; margin: auto;\" src=\"https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/mail-icon.png?alt=media\">" +
                "<h1 style=\"font-family:open Sans Helvetica, Arial, sans-serif; margin: 0; font-size:18px; padding: 2px; text-align: center;\">Playground Basketball</h1>" +
                "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:16px; margin: 0; padding: 2px; text-align: center;\">Please use the verification code below on the Playground Basketball website:</p>" +
                "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:18px; margin: 0; font-weight:bold;line-height:1;text-align:center;\">"+
                "<span style=\"color:#222222; background-color:#aad8ff;\">"+ user.getOptCode() + "</span></p>" +
                "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:16px; padding: 2px; margin: 0; text-align: center;\">It expires in 5 minutes.</p>" +
                "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:16px; padding: 2px; margin: 0; text-align: center;\">If you didn't request this, you can ignore this email or let us know.</p>" +
                "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:16px; padding: 2px; margin: 0; text-align: center;\">Thank!</p>" +
                "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:16px; padding: 2px; margin: 0; text-align: center;\">Playground Basketball</p>";
        emailService.sendHtmlTemplateMessage(user.getEmail(), emailSubject, htmlBody);
    }

    public void updateUser(User user) throws DataAccessException{
         userRepository.save(user);
    }
}
