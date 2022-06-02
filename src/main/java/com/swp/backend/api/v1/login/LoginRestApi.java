package com.swp.backend.api.v1.login;

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
import org.springframework.dao.DataAccessException;
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
    public ResponseEntity<String> login(@RequestBody(required = false) LoginRequest loginRequest){
        //Case empty body
        if(loginRequest == null){
            return ResponseEntity.badRequest().body("Request is empty body.");
        }
        //Case body wrong format
        if(!loginRequest.isValidRequest()){
            return ResponseEntity.badRequest().body("Can't determined username and password from request.");
        }
        //Get user from database
        AccountEntity account = accountService.findUserByUsername(loginRequest.getUsername());
        //Case can't find user with email or username provide.
        if(account == null){
            return ResponseEntity.badRequest().body("Account not exist or may be deleted by admin.");
        }

        //Checking password
        if(bCryptPasswordEncoder.matches(loginRequest.getPassword(), account.getPassword())){
            try {
                RoleEntity role = roleService.getRoleById(account.getRoleId());
                //Generate token
                String token = jwtTokenUtils.doGenerateToken(account.getUserId(), role.getRoleName());
                accountLoginService.saveLogin(account.getUserId(), token);
                //Save state login of user on app's database login context
                //Generate response
                LoginResponse loginResponse = LoginResponse.builder()
                        .userId(account.getUserId())
                        .email(account.getEmail())
                        .phone(account.getPhone())
                        .fullName(account.getFullName())
                        .isConfirmed(account.isConfirmed())
                        .role(role.getRoleName())
                        .avatar(account.getAvatar())
                        .accessToken(token)
                        .build();
                return ResponseEntity.ok().body(gson.toJson(loginResponse));
            }catch (DataAccessException dataAccessException){
                return ResponseEntity.internalServerError().body("Can't save login info on app's login database context.");
            }
        }else {
            //Case password not match
            return ResponseEntity.badRequest().body("Password is not match.");
        }
    }
}
