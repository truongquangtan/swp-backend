package com.swp.backend.api.v1.account.login;

import com.google.gson.Gson;
import com.swp.backend.entity.AccountEntity;
import com.swp.backend.entity.RoleEntity;
import com.swp.backend.exception.ErrorResponse;
import com.swp.backend.service.AccountLoginService;
import com.swp.backend.service.AccountService;
import com.swp.backend.service.RoleService;
import com.swp.backend.utils.JwtTokenUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1")
@AllArgsConstructor
public class LoginRestApi {
    Gson gson;
    AccountService accountService;
    RoleService roleService;
    BCryptPasswordEncoder bCryptPasswordEncoder;
    JwtTokenUtils jwtTokenUtils;
    AccountLoginService accountLoginService;

    @PostMapping("login")
    @Operation(summary = "Login by email/username/phone and password.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "400", description = "Missing request body, request body wrong format."),
                    @ApiResponse(responseCode = "500", description = "Can't generate jwt token or access database failed.")
            }
    )
    public ResponseEntity<String> login(@RequestBody(required = false) LoginRequest loginRequest) {
        try {
            //Case empty body
            if (loginRequest == null) {
                return ResponseEntity.badRequest().body("Request is empty body.");
            }
            //Case body wrong format
            if (!loginRequest.isValidRequest()) {
                ErrorResponse error = ErrorResponse.builder().message("Can't determined username and password from request.").build();
                return ResponseEntity.badRequest().body(gson.toJson(error));
            }
            //Get user from database
            AccountEntity account = accountService.findAccountByUsername(loginRequest.getUsername());
            //Case can't find user with email or username provide.
            if (account == null) {
                ErrorResponse error = ErrorResponse.builder().message("Incorrect email or password.").build();
                return ResponseEntity.badRequest().body(gson.toJson(error));
            }

            //Checking password
            if (bCryptPasswordEncoder.matches(loginRequest.getPassword(), account.getPassword())) {
                RoleEntity role = roleService.getRoleById(account.getRoleId());
                //Generate token
                String token = jwtTokenUtils.doGenerateToken(
                        account.getUserId(),
                        account.getFullName(),
                        account.getEmail(),
                        account.getPhone(),
                        role.getRoleName(),
                        account.isConfirmed()
                );
                accountLoginService.saveLogin(account.getUserId(), token);
                //Save state login of user on app's database login context
                //Generate response
                LoginResponse loginResponse = LoginResponse.builder()
                        .token(token)
                        .userId(account.getUserId())
                        .avatar(account.getAvatar())
                        .email(account.getEmail())
                        .phone(account.getPhone())
                        .role(role.getRoleName())
                        .fullName(account.getFullName())
                        .isConfirm(account.isConfirmed())
                        .build();
                return ResponseEntity.ok().body(gson.toJson(loginResponse));

            } else {
                //Case password not match
                ErrorResponse error = ErrorResponse.builder().message("Password incorrect.").build();
                return ResponseEntity.badRequest().body(gson.toJson(error));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            ErrorResponse error = ErrorResponse.builder().message("Server temp can't handle this login request.").build();
            return ResponseEntity.internalServerError().body(gson.toJson(error));
        }
    }
}
