package com.swp.backend.api.v1.account.update;

import com.google.gson.Gson;
import com.swp.backend.exception.ErrorResponse;
import com.swp.backend.model.SuccessResponseModel;
import com.swp.backend.service.AccountService;
import com.swp.backend.service.SecurityContextService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController()
@RequestMapping(value = "api/v1")
@AllArgsConstructor
public class UpdateAccountRestApi {
    private Gson gson;
    private AccountService accountService;
    private SecurityContextService securityContextService;

    @PostMapping(value = "me/update-profile")
    public ResponseEntity<String> updateAccount(@RequestParam(value = "avatar", required = false) MultipartFile file, @RequestParam("account") String data) {
        try {
            SecurityContext context = SecurityContextHolder.getContext();
            String userId = securityContextService.extractUsernameFromContext(context);
            boolean isUpdateSuccess;
            if (data != null) {
                UpdateAccountRequest accountRequest = gson.fromJson(data, UpdateAccountRequest.class);
                isUpdateSuccess = accountService.updateAccount(file, userId, accountRequest.getEmail(), accountRequest.getOldPassword(), accountRequest.getPassword(), accountRequest.getPhone());
            } else {
                isUpdateSuccess = accountService.updateAccount(file, userId, null, null, null, null);
            }
            if (isUpdateSuccess) {
                return ResponseEntity.ok(gson.toJson(SuccessResponseModel.builder().message("Update account success!").build()));
            } else {
                ErrorResponse errorResponse = ErrorResponse.builder().message("Update failed").build();
                return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
            }
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            ErrorResponse errorResponse = ErrorResponse.builder().stack(dataIntegrityViolationException.getMessage()).message("Email or phone already used by another account").build();
            return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
        } catch (Exception exception) {
            ErrorResponse errorResponse = ErrorResponse.builder().stack(exception.getMessage()).message("Server busy can't handle this request.").build();
            return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
        }
    }
}
