package com.swp.backend.api.v1.owner.invite_admin;

import com.google.gson.Gson;
import com.swp.backend.constance.RoleProperties;
import com.swp.backend.entity.AccountEntity;
import com.swp.backend.exception.ErrorResponse;
import com.swp.backend.service.AccountService;
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
@RequestMapping(value = "api/v1/owner")
@AllArgsConstructor
public class InviteAdminApi {
    Gson gson;
    AccountService accountService;

    @PostMapping("admin-register")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "400", description = "Missing request body, request body wrong format or email is already used by another account."),
                    @ApiResponse(responseCode = "500", description = "Can't generate jwt token or access database failed.")
            }
    )
    public ResponseEntity<String> register(@RequestBody(required = false) InviteAdminRequest inviteRequest){
        try {
            if(inviteRequest == null){
                ErrorResponse errorResponse = ErrorResponse.builder().message("Missing body.").build();
                return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
            }
            if(!inviteRequest.isValid())
            {
                return ResponseEntity.badRequest().body("Body is in incorrect format.");
            }

            String password = accountService.generatePasswordForAdminAccount();

            AccountEntity accountEntity = accountService.createAdminAccount(inviteRequest.getEmail(), inviteRequest.getFullName(), password, inviteRequest.getPhone());

            accountService.sendAdminAccountViaEmail(accountEntity.getEmail(), password);

            InviteAdminResponse response = new InviteAdminResponse("Invite admin success", accountEntity.getEmail(), password);

            return ResponseEntity.ok().body(gson.toJson(response));
        }catch (DataAccessException dataAccessException){
            return ResponseEntity.badRequest().body(dataAccessException.getMessage());
        }catch (Exception exception){
            exception.printStackTrace();
            return ResponseEntity.internalServerError().body("Server temp error.");
        }
    }
}
