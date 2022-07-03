package com.swp.backend.service;

import com.swp.backend.constance.RoleProperties;
import com.swp.backend.entity.AccountEntity;
import com.swp.backend.entity.AccountOtpEntity;
import com.swp.backend.entity.RoleEntity;
import com.swp.backend.model.AccountModel;
import com.swp.backend.myrepository.AccountCustomRepository;
import com.swp.backend.repository.AccountRepository;
import com.swp.backend.repository.RoleRepository;
import com.swp.backend.utils.DateHelper;
import com.swp.backend.utils.RegexHelper;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AccountService {
    public static final String DISABLED_USER_REASON = "The owner is disabled by admin";

    private final AccountRepository accountRepository;
    private final RoleService roleService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AccountLoginService accountLoginService;
    private final RoleRepository roleRepository;
    private final YardService yardService;
    private final FirebaseStoreService firebaseStoreService;
    private final InactivationService inactivationService;
    private final ReactivationService reactivationService;
    private final AccountCustomRepository accountCustomRepository;

    public AccountEntity updatePassword(String username, String password) throws DataAccessException {
        AccountEntity account = findAccountByUsername(username);
        if (account == null) {
            return null;
        }
        account.setPassword(passwordEncoder.encode(password));
        accountRepository.save(account);
        accountLoginService.deleteAllLogin(username);
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
        String uuid = UUID.randomUUID().toString();
        RoleEntity roleEntity = roleService.getRoleByRoleName(roleName);
        Timestamp now = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);
        AccountEntity accountEntity = AccountEntity.builder()
                .userId(uuid)
                .email(email)
                .fullName(fullName)
                .phone(phone)
                .password(passwordEncoder.encode(password))
                .roleId(roleEntity.getId())
                .createAt(now)
                .build();
        try {
            accountRepository.save(accountEntity);
            return accountEntity;
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            throw new DataIntegrityViolationException("Email or phone already use by another account.");
        }
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
                    .createAt(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE))
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

    public void modifyUserInformation(String userId, String fullName, String phone, Boolean isActive) {
        AccountEntity account = accountRepository.findUserEntityByUserId(userId);
        if (account == null) {
            throw new RuntimeException("Can not find account.");
        }
        if (fullName != null) account.setFullName(fullName);
        if (phone != null) {
            if (phone.equals("")) phone = null;
            account.setPhone(phone);
        }
        if (isActive == null) {
            accountRepository.save(account);
            return;
        }
        if (isActive && !account.isActive()) {
            account.setActive(true);
            new Thread(() -> {
                try {
                    yardService.reactiveAllYardsOfOwner(userId);
                    yardService.getAllYardEntityOfOwner(userId).stream().forEach(yard -> {
                        if(!yard.isActive())
                            reactivationService.processReactiveYard(yard.getId());
                    });
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }).start();
        } else if (!isActive && account.isActive()) {
            account.setActive(false);
            new Thread(() -> {
                try {
                    accountLoginService.deleteAllLogin(userId);
                    yardService.getAllYardEntityOfOwner(userId).stream().forEach(yard -> {
                        if(yard.isActive())
                            inactivationService.processInactivateYard(userId, yard.getId(), DISABLED_USER_REASON);
                    });
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }).start();
        }
        accountRepository.save(account);
    }

    public void updateUser(AccountEntity accountEntity) throws DataAccessException {
        accountRepository.save(accountEntity);
    }

    public List<AccountModel> getAllAccountHasRoleUserByPage(int page, int itemsPerPage) {
        List<RoleEntity> roleEntities = roleService.getAllRole();
        if (roleEntities == null || roleEntities.size() == 0) {
            return null;
        }
        HashMap<Integer, String> roleMap = new HashMap<>();
        roleEntities.forEach(role -> roleMap.put(role.getId(), role.getRoleName()));

        int startIndex = itemsPerPage * (page - 1);
        int endIndex = startIndex + itemsPerPage - 1;
        int maxIndex = accountCustomRepository.countAllUserOrOwnerAccounts() - 1;
        endIndex = Math.min(endIndex, maxIndex);

        if (startIndex > endIndex) return new ArrayList<>();

        List<AccountEntity> accounts = accountCustomRepository.getAllUserOrOwnerAccountsByPage(startIndex, endIndex);
        if (accounts == null || accounts.size() == 0) {
            return new ArrayList<>();
        }
        return accounts.stream().map(account -> AccountModel.builder()
                .userId(account.getUserId())
                .fullName(account.getFullName())
                .email(account.getEmail())
                .phone(account.getPhone())
                .isActive(account.isActive())
                .isConfirmed(account.isConfirmed())
                .avatar(account.getAvatar())
                .role(roleMap.get(account.getRoleId()))
                .createAt(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(account.getCreateAt()))
                .build()).collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public int countAllAccountHasRoleUser() {
        return accountRepository.countAccountEntitiesByRoleIdOrRoleId(1, 3);
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
                accountLoginService.deleteAllLogin(userId);
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

    public List<?> searchAccount(Integer itemsPerPage, Integer page, Integer role, String keyword, String status, List<String> sortBy, String sort) {
        return accountCustomRepository.searchAccount(itemsPerPage, page, role, keyword, status, sortBy, sort);
    }

    public int getMaxResultSearch(Integer role, String keyword, String status) {
        return accountCustomRepository.countMaxResultSearchAccount(role, keyword, status);
    }

    public AccountModel updateAccount(MultipartFile avatar, String userId, String fullName, String phone) throws IOException, DataAccessException {
        AccountEntity account = findAccountByUsername(userId);
        if (account == null) {
            return null;
        }

        if (avatar != null) {
            String url = firebaseStoreService.uploadFile(avatar);
            account.setAvatar(url);
        }

        if (phone != null && phone.matches(RegexHelper.PHONE_REGEX_LOCAL)) {
            account.setPhone(phone);
        }

        if(fullName  != null && fullName.trim().length() > 0){
            account.setFullName(fullName.trim());
        }

        accountRepository.save(account);
        RoleEntity role = roleService.getRoleById(account.getRoleId());
        return AccountModel.builder()
                .role(role.getRoleName())
                .userId(account.getUserId())
                .fullName(account.getFullName())
                .avatar(account.getAvatar())
                .createAt(account.getCreateAt().toString())
                .phone(account.getPhone())
                .isConfirmed(account.isConfirmed())
                .email(account.getEmail())
                .build();
    }

    public AccountModel verifyCurrentPassword(String userId, String password) throws DataAccessException{
        if(userId == null || password == null){
            return null;
        }
        AccountEntity account = accountRepository.findUserEntityByUserId(userId);
        if(account == null){
            return null;
        }
        if(passwordEncoder.matches(password, account.getPassword())){
            RoleEntity role = roleService.getRoleById(account.getRoleId());
            return AccountModel.builder()
                    .role(role.getRoleName())
                    .userId(account.getUserId())
                    .fullName(account.getFullName())
                    .avatar(account.getAvatar())
                    .createAt(account.getCreateAt().toString())
                    .phone(account.getPhone())
                    .isConfirmed(account.isConfirmed())
                    .email(account.getEmail())
                    .build();
        }
        return null;
    }
}