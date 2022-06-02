package com.swp.backend.api.v1.logout;

import com.google.gson.Gson;
import com.swp.backend.exception.ErrorResponse;
import com.swp.backend.service.AccountLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@AllArgsConstructor
public class LogoutRestApi {

    AccountLoginService accountLoginService;
    Gson gson;

    @Operation(description = "Required attach header access token.")
    @ApiResponse(responseCode = "500", description = "Logout failed, delete token context login failed.")
    @GetMapping(value = "logout")
    public ResponseEntity<String> logout(){
        //Get current user from spring security context container
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user instanceof UserDetails){
            String userId = ((UserDetails) user).getUsername();
            try {
                accountLoginService.expireLogin(userId);
            }catch (DataAccessException dataAccessException){
                return  ResponseEntity.internalServerError().body("Logout failed. " + dataAccessException.getMessage());
            }
        }
        return ResponseEntity.ok("Logout success!");
    }
}
