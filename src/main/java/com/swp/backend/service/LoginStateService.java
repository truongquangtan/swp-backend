package com.swp.backend.service;

import com.swp.backend.entity.LoginState;
import com.swp.backend.repository.LoginStateRepository;
import org.springframework.stereotype.Service;

@Service
public class LoginStateService {
    LoginStateRepository loginStateRepository;

    public LoginStateService(LoginStateRepository loginStateRepository) {
        this.loginStateRepository = loginStateRepository;
    }

    //Find state-login of user on app's login-context database.
    public LoginState findLogin(String userId){
        return loginStateRepository.findLoginStateByUserId(userId);
    }

    //Save state-login of user on app's login-context database.
    public void saveLogin(String userId, String token){
        LoginState login = findLogin(userId);
        if(login == null){
            login = LoginState.builder().userId(userId).build();
        }
        login.setLogout(false);
        login.setAccessToken(token);
        try {
            loginStateRepository.save(login);
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    //Destroy state-login of user on app's login-context database.
    public void expireLogin(String userId){
           LoginState login = loginStateRepository.findLoginStateByUserId(userId);
           login.setLogout(true);
           loginStateRepository.save(login);
    }
}
