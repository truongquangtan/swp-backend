package com.swp.backend.api.v1.login;

import com.google.gson.Gson;
import com.swp.backend.entity.UserEntity;
import com.swp.backend.exception.ErrorResponse;
import com.swp.backend.model.JwtToken;
import com.swp.backend.service.LoginStateService;
import com.swp.backend.service.UserService;
import com.swp.backend.utils.JwtTokenUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1")
public class LoginRestApi {
    Gson gson;
    UserService userService;
    BCryptPasswordEncoder bCryptPasswordEncoder;
    JwtTokenUtils jwtTokenUtils;
    LoginStateService loginStateService;

    public LoginRestApi(Gson gson, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder, JwtTokenUtils jwtTokenUtils, LoginStateService loginStateService) {
        this.gson = gson;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtTokenUtils = jwtTokenUtils;
        this.loginStateService = loginStateService;
    }

    @PostMapping("login")
    @Operation(summary = "Login by email/username/phone and password.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "400", description = "Missing request body, request body wrong format."),
                    @ApiResponse(responseCode = "500", description = "Can't generate jwt token or access database failed.")
            }
    )
    public ResponseEntity<String> login(@RequestBody(required = false) LoginRequest loginRequest){
        //Case empty body
        if(loginRequest == null){
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .error("auth-006")
                    .message("Missing body.")
                    .details("Request is empty body.")
                    .build();
            return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
        }
        //Case body wrong format
        if(!loginRequest.isValidRequest()){
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .error("auth-007")
                    .message("Bad body.")
                    .details("Can't determined username and password from request.")
                    .build();
            return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
        }
        //Get user from database
        UserEntity loginUserEntity = userService.findUserByUsername(loginRequest.getUsername());
        //Case can't find user with email or username provide.
        if(loginUserEntity == null){
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .error("auth-008")
                    .message("Username or email notfound.")
                    .details("Account not exist or may be deleted by admin.")
                    .build();
            return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
        }
        //Checking password
        if(bCryptPasswordEncoder.matches(loginRequest.getPassword(), loginUserEntity.getPassword())){
            try {
                //Generate token
                JwtToken token = jwtTokenUtils.doGenerateToken(loginUserEntity);
                //Save state login of user on app's database login context
                loginStateService.saveLogin(loginUserEntity.getUserId(), token.getToken());
                //Generate response
                LoginResponse loginResponse = LoginResponse.builder()
                        .userId(loginUserEntity.getUserId())
                        .email(loginUserEntity.getEmail())
                        .phone(loginUserEntity.getPhone())
                        .fullName(loginUserEntity.getFullName())
                        .isConfirmed(loginUserEntity.isConfirmed())
                        .role(loginUserEntity.getRole())
                        .avatar(loginUserEntity.getAvatar())
                        .token(token)
                        .build();
                return ResponseEntity.ok().body(gson.toJson(loginResponse));
            }catch (DataAccessException dataAccessException){
                //Save state login failed
                ErrorResponse errorResponse = ErrorResponse.builder()
                        .error("auth-009")
                        .message("Login failed.")
                        .details("Can't save login info on app's login database context.")
                        .build();
                return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
            }
        }else {
            //Case password not match
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .error("auth-010")
                    .message("Password is not match.")
                    .details("Password incorrect.")
                    .build();
            return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
        }
    }
}
