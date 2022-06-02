package com.swp.backend.api.v1.register;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.swp.backend.constance.RoleProperties;
import com.swp.backend.entity.AccountEntity;
import com.swp.backend.entity.AccountOtpEntity;
import com.swp.backend.service.AccountLoginService;
import com.swp.backend.service.OtpStateService;
import com.swp.backend.service.AccountService;
import com.swp.backend.utils.JwtTokenUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1")
@AllArgsConstructor
public class RegisterRestApi {
    Gson gson;
    AccountService accountService;
    JwtTokenUtils jwtTokenUtils;
    AccountLoginService accountLoginService;
    OtpStateService otpStateService;

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
            return ResponseEntity.badRequest().body("Missing body.");
        }
        //Case request body missing required username, password, email.
        if(!registerRequest.isValidRequest()){
            return ResponseEntity.badRequest().body("Request body incorrect formant");
        }

        try {
            //Call user-service's create new user method
            AccountEntity accountEntity = accountService.createUser(registerRequest.getEmail(), registerRequest.getFullName(), registerRequest.getPassword(), registerRequest.getPhone(), RoleProperties.ROLE_USER);
            //Call otp-service's otp generate method
            AccountOtpEntity accountOtpEntity = otpStateService.generateOtp(accountEntity.getUserId());
            //Call user-service's send mail asynchronous method
            accountService.sendOtpVerifyAccount(accountEntity, accountOtpEntity);
            //Generate login token
            String token = jwtTokenUtils.doGenerateToken(accountEntity.getUserId(), RoleProperties.ROLE_USER);
            //Save login state on app's login context-database
            accountLoginService.saveLogin(accountEntity.getUserId(), token);
            //Generate response
            RegisterResponse registerResponse = RegisterResponse.builder()
                    .userId(accountEntity.getUserId())
                    .email(accountEntity.getEmail())
                    .role(RoleProperties.ROLE_USER)
                    .isConfirmed(accountEntity.isConfirmed())
                    .token(token)
                    .build();
            return ResponseEntity.ok(gson.toJson(registerResponse));
        }catch (JsonParseException jsonException){
            return ResponseEntity.internalServerError().body("Jwt token generate failed.");
        }catch (DataAccessException dataAccessException){
            return ResponseEntity.internalServerError().body(dataAccessException.getMessage());
        }
    }

}
