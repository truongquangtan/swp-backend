package com.swp.backend.service;

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

    public void updateUser(AccountEntity accountEntity) throws DataAccessException{
         accountRepository.save(accountEntity);
    }

    public List<AccountEntity> getAllUserHasRoleUser(){
        return accountRepository.findAccountEntitiesByRoleIdOrRoleId(1, 3);
    }
}
