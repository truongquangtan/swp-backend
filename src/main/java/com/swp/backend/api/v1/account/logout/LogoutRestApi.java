package com.swp.backend.api.v1.account.logout;

import com.swp.backend.service.AccountLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
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

    @Operation(description = "Required attach header access token.")
    @ApiResponse(responseCode = "500", description = "Logout failed, delete token context login failed.")
    @GetMapping(value = "logout")
    public ResponseEntity<String> logout(){
        try {
            //Get current user from spring security context container
            Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if(user instanceof UserDetails){
                String userId = ((UserDetails) user).getUsername();
                accountLoginService.expireLogin(userId);
            }
            return ResponseEntity.ok("Logout success!");
        }catch (Exception exception){
            exception.printStackTrace();
            return  ResponseEntity.internalServerError().body("Logout failed.");
        }
    }
}
