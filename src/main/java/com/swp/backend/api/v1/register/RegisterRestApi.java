package com.swp.backend.api.v1.register;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1")
public class RegisterRestApi {
    Gson gson;
    UserService userService;
    JwtTokenUtils jwtTokenUtils;
    LoginStateService loginStateService;

    public RegisterRestApi(Gson gson, UserService userService, JwtTokenUtils jwtTokenUtils, LoginStateService loginStateService) {
        this.gson = gson;
        this.userService = userService;
        this.jwtTokenUtils = jwtTokenUtils;
        this.loginStateService = loginStateService;
    }

    @PostMapping("register")
    @Operation(summary = "Register user by email and password.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "400", description = "Missing request body, request body wrong format or email is already used by another account."),
                    @ApiResponse(responseCode = "500", description = "Can't generate jwt token or access database failed.")
            }
    )
    public ResponseEntity<String> register(@RequestBody(required = false) RegisterRequest registerRequest){
        //Case request empty body
        if(registerRequest == null){
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .error("auth-011")
                    .message("Missing body.")
                    .details("Request is empty body.")
                    .build();
            return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
        }
        //Case request body missing required username, password, email.
        if(!registerRequest.isValidRequest()){
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .error("auth-012")
                    .message("Missing body.")
                    .details("Request is not match required.")
                    .build();
            return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
        }

        try {
            //Call user-service's create new user method
            User user = userService.createUser(registerRequest.getEmail(), registerRequest.getFullName(), registerRequest.getPassword(), registerRequest.getPhone(), "USER");
            //Call user-service's  send mail asynchronous method
            userService.sendOtpVerifyAccount(user);
            //Generate login token
            JwtToken token = jwtTokenUtils.doGenerateToken(user);
            //Save login state on app's login context-database
            loginStateService.saveLogin(user.getUserId(), token.getToken());
            //Generate response
            RegisterResponse registerResponse = RegisterResponse.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .createAt(user.getCreateAt())
                    .role(user.getRole())
                    .isConfirmed(user.isConfirmed())
                    .token(token).build();
            return ResponseEntity.ok(gson.toJson(registerResponse));
        }catch (JsonParseException jsonException){
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .error("auth-013")
                    .message("Jwt token generate failed.")
                    .details("Can't generate jwt token or resolve response failed.")
                    .build();
            return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
        }catch (DataAccessException dataAccessException){
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .error("auth-014")
                    .message("Create user failed.")
                    .details(dataAccessException.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
        }
    }

}
