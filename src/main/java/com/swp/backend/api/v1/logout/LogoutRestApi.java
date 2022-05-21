package com.swp.backend.api.v1.logout;

import com.google.gson.Gson;
import com.swp.backend.exception.ErrorResponse;
import com.swp.backend.service.LoginStateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
public class LogoutRestApi {

    LoginStateService loginStateService;
    Gson gson;

    public LogoutRestApi(LoginStateService loginStateService, Gson gson) {
        this.loginStateService = loginStateService;
        this.gson = gson;
    }

    @Operation(description = "Required attach header access token.")
    @ApiResponse(responseCode = "500", description = "Logout failed, delete token context login failed.")
    @GetMapping(value = "logout")
    public ResponseEntity<String> logout(){
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user instanceof UserDetails){
            String userId = ((UserDetails) user).getUsername();
            try {
                loginStateService.expireLogin(userId);
            }catch (DataAccessException dataAccessException){
                ErrorResponse errorResponse = ErrorResponse.builder()
                        .error("auth-015")
                        .message("Logout failed.")
                        .details("Delete token context login failed. " + dataAccessException.getMessage())
                        .build();
                return  ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
            }
        }
        return ResponseEntity.ok("Logout success!");
    }
}
