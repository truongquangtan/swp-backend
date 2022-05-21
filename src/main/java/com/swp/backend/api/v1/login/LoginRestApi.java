package com.swp.backend.api.v1.login;

import com.google.gson.Gson;
import com.swp.backend.entity.User;
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
        if(loginRequest.getPassword() == null || loginRequest.getUsername() == null){
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .error("auth-007")
                    .message("Bad body.")
                    .details("Can't determined username and password from request.")
                    .build();
            return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
        }
        //Get user from database
        User loginUser = userService.findUserByUsername(loginRequest.getUsername());
        //Case can't find user with email or username provide.
        if(loginUser == null){
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .error("auth-008")
                    .message("Username or email notfound.")
                    .details("Account not exist or may be deleted by admin.")
                    .build();
            return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
        }
        //Checking password
        if(bCryptPasswordEncoder.matches(loginRequest.getPassword(), loginUser.getPassword())){
            try {
                JwtToken token = jwtTokenUtils.doGenerateToken(loginUser);
                LoginResponse loginResponse = LoginResponse.builder()
                        .userId(loginUser.getUserId())
                        .email(loginUser.getEmail())
                        .phone(loginUser.getPhone())
                        .fullName(loginUser.getFullName())
                        .isConfirmed(loginUser.isConfirmed())
                        .role(loginUser.getRole())
                        .token(token)
                        .build();
                loginStateService.saveLogin(loginUser.getUserId(), token.getToken());
                return ResponseEntity.ok().body(gson.toJson(loginResponse));
            }catch (DataAccessException dataAccessException){
                ErrorResponse errorResponse = ErrorResponse.builder()
                        .error("auth-009")
                        .message("Login failed.")
                        .details("Can't save login info on app's login database context.")
                        .build();
                return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
            }
        }else {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .error("auth-010")
                    .message("Password is not match.")
                    .details("Password incorrect.")
                    .build();
            return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
        }
    }
}
