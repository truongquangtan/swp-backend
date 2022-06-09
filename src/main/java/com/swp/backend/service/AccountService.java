package com.swp.backend.service;

import com.swp.backend.constance.RoleProperties;
import com.swp.backend.entity.AccountEntity;
import com.swp.backend.entity.AccountOtpEntity;
import com.swp.backend.entity.RoleEntity;
import com.swp.backend.repository.AccountRepository;
import com.swp.backend.utils.RegexHelper;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final RoleService roleService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AccountEntity updatePassword(String username, String password) throws DataAccessException{
        AccountEntity account = findAccountByUsername(username);
        if(account == null){
            return null;
        }
        account.setPassword(passwordEncoder.encode(password));
        accountRepository.save(account);
        return account;
    }

    public String generatePasswordForAdminAccount()
    {
        int PASS_GEN_LENGTH = 15;
        String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String specialCharacters = "!@#$";
        String numbers = "1234567890";
        String combinedChars = capitalCaseLetters + lowerCaseLetters + specialCharacters + numbers;
        Random random = new Random();
        char[] password = new char[PASS_GEN_LENGTH];

        password[0] = lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length()));
        password[1] = capitalCaseLetters.charAt(random.nextInt(capitalCaseLetters.length()));
        password[2] = specialCharacters.charAt(random.nextInt(specialCharacters.length()));
        password[3] = numbers.charAt(random.nextInt(numbers.length()));

        for(int i = 4; i< PASS_GEN_LENGTH ; i++) {
            password[i] = combinedChars.charAt(random.nextInt(combinedChars.length()));
        }
        return String.copyValueOf(password);
    }

    public AccountEntity findAccountByUsername(String username){
        //Case username is null
        if(username == null){
            return null;
        }
        //Find user by username, phone, or password
        if(username.matches(RegexHelper.EMAIL_REGEX)){
            return accountRepository.findUserEntityByEmail(username);
        } else if (username.matches("\\d+")){
            return  accountRepository.findUserEntityByPhone(username);
        }else {
            return accountRepository.findUserEntityByUserId(username);
        }
    }

    public AccountEntity createAccount(String email, String fullName, String password, String phone, String roleName) throws DataAccessException{
        AccountEntity account = findAccountByUsername(email);
        if(account != null){
            throw new DataIntegrityViolationException("Email already use by another account.");
        }
        String uuid = UUID.randomUUID().toString();
        RoleEntity roleEntity = roleService.getRoleByRoleName(roleName);
        AccountEntity accountEntity = AccountEntity.builder()
            .userId(uuid)
            .email(email)
            .fullName(fullName)
            .phone(phone)
            .password(passwordEncoder.encode(password))
            .roleId(roleEntity.getId())
        .build();
        accountRepository.save(accountEntity);
        return accountEntity;
    }
    public void sendOtpVerifyAccount(AccountEntity accountEntity, AccountOtpEntity accountOtpEntity){
        String emailSubject = "VERIFY PLAYGROUND BASKETBALL CODE";
        String htmlBody =
                "<img style=\"display: block; width: 60px; padding: 2px; height: 60px; margin: auto;\" src=\"https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/mail-icon.png?alt=media\">" +
                "<h1 style=\"font-family:open Sans Helvetica, Arial, sans-serif; margin: 0; font-size:18px; padding: 2px; text-align: center;\">Playground Basketball</h1>" +
                "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:16px; margin: 0; padding: 2px; text-align: center;\">Please use the verification code below on the Playground Basketball website:</p>" +
                "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:18px; margin: 0; font-weight:bold;line-height:1;text-align:center;\">"+
                "<span style=\"color:#222222; background-color:#aad8ff;\">"+ accountOtpEntity.getOtpCode() + "</span></p>" +
                "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:16px; padding: 2px; margin: 0; text-align: center;\">It expires in 5 minutes.</p>" +
                "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:16px; padding: 2px; margin: 0; text-align: center;\">If you didn't request this, you can ignore this email or let us know.</p>" +
                "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:16px; padding: 2px; margin: 0; text-align: center;\">Thank!</p>" +
                "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:16px; padding: 2px; margin: 0; text-align: center;\">Playground Basketball</p>";
        emailService.sendHtmlTemplateMessage(accountEntity.getEmail(), emailSubject, htmlBody);
    }


    public AccountEntity createOwnerAccount(String email, String fullName, String password, String phone) throws DataAccessException{
        AccountEntity account = findAccountByUsername(email);
        if(account != null){
            throw new DataIntegrityViolationException("Email already use by another account.");
        }
        String uuid = UUID.randomUUID().toString();
        RoleEntity roleEntity = roleService.getRoleByRoleName(RoleProperties.ROLE_ADMIN);
        AccountEntity accountEntity = AccountEntity.builder()
                .userId(uuid)
                .email(email)
                .fullName(fullName)
                .phone(phone)
                .password(passwordEncoder.encode(password))
                .roleId(roleEntity.getId())
                .isConfirmed(true)
                .isActive(true)
                .build();
        accountRepository.save(accountEntity);
        return accountEntity;
    }
    public void sendOwnerAccountViaEmail(String email, String password)
    {
        String emailSubject = "INVITE TO USE YARD BOOKING ADMIN ACCOUNT";
        String htmlBody = "<img style=\"display: block; width: 60px; padding: 2px; height: 60px; margin: auto;\" src=\"https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/mail-icon.png?alt=media\">" +
    "<h1 style=\"font-family:open Sans Helvetica, Arial, sans-serif; margin: 0; font-size:18px; padding: 2px; text-align: center;\">Playground Basketball</h1>" +
    "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:16px; margin: 0; padding: 2px; text-align: center;\">Thank you for your cooperation!</p>" +
    "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:16px; margin: 0; padding: 2px; text-align: center;\">Your account information:</p>" +
    "<br>" +
    "<table border=\"1\" style=\"margin: 0 auto;\">" +
        "<tr>" +
            "<td style=\"text-align: center;\">Email</td>" +
            "<td style=\"text-align: center;\">"+ email +"</td>" +
        "</tr>" +
        "<tr>" +
            "<td style=\"text-align: center;\">" + "Password" +"</td>" +
            "<td style=\"text-align: center;\">"+ password +"</td>" +
        "</tr>" +
    "</table>" +
    "<p style=\"text-align: center;\">--------------</p>";
        emailService.sendHtmlTemplateMessage(email, emailSubject, htmlBody);
    }

    public void updateUser(AccountEntity accountEntity) throws DataAccessException{
         accountRepository.save(accountEntity);
    }

    public List<AccountEntity> getAllUserHasRoleUser(){
        return accountRepository.findAccountEntitiesByRoleIdOrRoleId(1, 3);
    }
}
