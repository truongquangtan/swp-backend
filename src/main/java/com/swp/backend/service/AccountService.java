package com.swp.backend.service;

import com.swp.backend.constance.RoleProperties;
import com.swp.backend.entity.AccountEntity;
import com.swp.backend.entity.AccountOtpEntity;
import com.swp.backend.entity.RoleEntity;
import com.swp.backend.model.AccountModel;
import com.swp.backend.repository.AccountRepository;
import com.swp.backend.repository.RoleRepository;
import com.swp.backend.utils.RegexHelper;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final RoleService roleService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AccountLoginService accountLoginService;
    private final RoleRepository roleRepository;

    public AccountEntity updatePassword(String username, String password) throws DataAccessException {
        AccountEntity account = findAccountByUsername(username);
        if (account == null) {
            return null;
        }
        account.setPassword(passwordEncoder.encode(password));
        accountRepository.save(account);
        accountLoginService.deleteLogin(username);
        return account;
    }

    public AccountEntity findAccountByUsername(String username) {
        //Case username is null
        if (username == null) {
            return null;
        }

        //Find user by username, phone, or password
        if (username.matches(RegexHelper.EMAIL_REGEX)) {
            return accountRepository.findUserEntityByEmail(username);
        } else if (username.matches("\\d+")) {
            return accountRepository.findUserEntityByPhone(username);
        } else {
            return accountRepository.findUserEntityByUserId(username);
        }
    }

    public AccountEntity createAccount(String email, String fullName, String password, String phone, String roleName) throws DataAccessException {
        AccountEntity account = findAccountByUsername(email);
        if (account != null) {
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

    public void sendOtpVerifyAccount(AccountEntity accountEntity, AccountOtpEntity accountOtpEntity) {
        String emailSubject = "VERIFY PLAYGROUND BASKETBALL CODE";
        String htmlBody =
                "<img style=\"display: block; width: 60px; padding: 2px; height: 60px; margin: auto;\" src=\"https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/mail-icon.png?alt=media\">" +
                        "<h1 style=\"font-family:open Sans Helvetica, Arial, sans-serif; margin: 0; font-size:18px; padding: 2px; text-align: center;\">Playground Basketball</h1>" +
                        "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:16px; margin: 0; padding: 2px; text-align: center;\">Please use the verification code below on the Playground Basketball website:</p>" +
                        "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:18px; margin: 0; font-weight:bold;line-height:1;text-align:center;\">" +
                        "<span style=\"color:#222222; background-color:#aad8ff;\">" + accountOtpEntity.getOtpCode() + "</span></p>" +
                        "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:16px; padding: 2px; margin: 0; text-align: center;\">It expires in 5 minutes.</p>" +
                        "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:16px; padding: 2px; margin: 0; text-align: center;\">If you didn't request this, you can ignore this email or let us know.</p>" +
                        "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:16px; padding: 2px; margin: 0; text-align: center;\">Thank!</p>" +
                        "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:16px; padding: 2px; margin: 0; text-align: center;\">Playground Basketball</p>";
        emailService.sendHtmlTemplateMessage(accountEntity.getEmail(), emailSubject, htmlBody);
    }

    public AccountEntity createOwnerAccount(String email, String fullName, String password, String phone) throws DataAccessException {
        try {
            String uuid = UUID.randomUUID().toString();
            RoleEntity roleEntity = roleService.getRoleByRoleName(RoleProperties.ROLE_OWNER);
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
        } catch (DataAccessException dataAccessException) {
            if (dataAccessException instanceof DataIntegrityViolationException) {
                throw new DataIntegrityViolationException("Email or phone already use by another account.");
            } else {
                throw dataAccessException;
            }
        }
    }

    public void sendOwnerAccountViaEmail(String email, String password) {
        String emailSubject = "INVITE TO USE YARD BOOKING ADMIN ACCOUNT";
        String htmlBody = "<img style=\"display: block; width: 60px; padding: 2px; height: 60px; margin: auto;\" src=\"https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/mail-icon.png?alt=media\">" +
                "<h1 style=\"font-family:open Sans Helvetica, Arial, sans-serif; margin: 0; font-size:18px; padding: 2px; text-align: center;\">Playground Basketball</h1>" +
                "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:16px; margin: 0; padding: 2px; text-align: center;\">Thank you for your cooperation!</p>" +
                "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:16px; margin: 0; padding: 2px; text-align: center;\">Your account information:</p>" +
                "<br>" +
                "<table border=\"1\" style=\"margin: 0 auto;\">" +
                "<tr>" +
                "<td style=\"text-align: center;\">Email</td>" +
                "<td style=\"text-align: center;\">" + email + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td style=\"text-align: center;\">" + "Password" + "</td>" +
                "<td style=\"text-align: center;\">" + password + "</td>" +
                "</tr>" +
                "</table>" +
                "<p style=\"text-align: center;\">--------------</p>";
        emailService.sendHtmlTemplateMessage(email, emailSubject, htmlBody);
    }

    public void updateUser(AccountEntity accountEntity) throws DataAccessException {
        accountRepository.save(accountEntity);
    }

    public List<AccountModel> getAllUserHasRoleUser() {
        List<RoleEntity> roleEntities = roleService.getAllRole();
        if (roleEntities == null || roleEntities.size() == 0) {
            return null;
        }
        HashMap<Integer, String> roleMap = new HashMap<>();
        roleEntities.forEach(role -> {
            roleMap.put(role.getId(), role.getRoleName());
        });

        List<AccountEntity> accounts = accountRepository.findAccountEntitiesByRoleIdOrRoleId(1, 3);
        if (accounts == null || accounts.size() == 0) {
            return null;
        }
        return accounts.stream().map(account -> {
            return AccountModel.builder()
                    .userId(account.getUserId())
                    .fullName(account.getFullName())
                    .email(account.getEmail())
                    .phone(account.getPhone())
                    .isActive(account.isActive())
                    .isConfirmed(account.isConfirmed())
                    .avatar(account.getAvatar())
                    .role(roleMap.get(account.getRoleId()))
                    .build();
        }).collect(Collectors.toList());
    }

    public boolean deactivateAccount(String userId) throws DataAccessException {
        AccountEntity account = findAccountByUsername(userId);
        if (account == null) {
            return false;
        }
        account.setActive(false);
        accountRepository.save(account);
        new Thread(() -> {
            try {
                accountLoginService.deleteLogin(userId);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }).start();
        return true;
    }

    public boolean reactivateAccount(String userId) throws DataAccessException {
        AccountEntity account = findAccountByUsername(userId);
        if (account == null) {
            return false;
        }
        account.setActive(true);
        accountRepository.save(account);
        return true;
    }

    public String getRoleFromUserId(String userId) {
        AccountEntity accountEntity = accountRepository.findUserEntityByUserId(userId);
        int roleId = accountEntity.getRoleId();
        return roleRepository.findRoleEntityById(roleId).getRoleName();
    }
}
