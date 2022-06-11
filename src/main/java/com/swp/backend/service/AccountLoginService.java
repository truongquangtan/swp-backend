package com.swp.backend.service;

import com.swp.backend.entity.AccountLoginEntity;
import com.swp.backend.repository.AccountLoginRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountLoginService {
    AccountLoginRepository accountLoginRepository;

    //Find state-login of user on app's login-context database.
    public AccountLoginEntity findLogin(String userId){
        return accountLoginRepository.findLoginStateByUserId(userId);
    }

    //Save state-login of user on app's login-context database.
    public void saveLogin(String userId, String token){
        AccountLoginEntity login = findLogin(userId);
        if(login == null){
            login = AccountLoginEntity.builder().userId(userId).build();
        }
        login.setLogout(false);
        login.setAccessToken(token);
        try {
            accountLoginRepository.save(login);
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    //Destroy state-login of user on app's login-context database.
    public void expireLogin(String userId){
           AccountLoginEntity login = accountLoginRepository.findLoginStateByUserId(userId);
           login.setLogout(true);
           accountLoginRepository.save(login);
    }

    public void deleteLogin(String userId){
        AccountLoginEntity login = accountLoginRepository.findLoginStateByUserId(userId);
        if(login != null){
            accountLoginRepository.deleteById(login.getId());
        }
    }
}
