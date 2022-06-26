package com.swp.backend.api.v1.owner.voucher;

import com.google.gson.Gson;
import com.swp.backend.exception.ErrorResponse;
import com.swp.backend.model.RequestPageModel;
import com.swp.backend.model.VoucherModel;
import com.swp.backend.service.SecurityContextService;
import com.swp.backend.service.VoucherService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("api/v1/owners/me")
@RestController
@AllArgsConstructor
public class OwnerVoucherRestApi {
    private Gson gson;
    private VoucherService voucherService;
    private SecurityContextService securityContextService;

    @PostMapping("vouchers/create")
    public ResponseEntity<String> createVouchers(@RequestBody(required = false) VoucherModel voucherModel) {
        try {
            if (voucherModel == null) {
                ErrorResponse response = ErrorResponse.builder().message("Missing body!").build();
                return ResponseEntity.badRequest().body(gson.toJson(response));
            }
            SecurityContext securityContext = SecurityContextHolder.getContext();
            String accountId = securityContextService.extractUsernameFromContext(securityContext);
            voucherService.createVoucher(voucherModel, accountId);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("vouchers")
    public ResponseEntity<String> getAllVouchers(@RequestBody(required = false) RequestPageModel pageModel) {
        try {
            VoucherResponse response;
            SecurityContext securityContext = SecurityContextHolder.getContext();
            String accountId = securityContextService.extractUsernameFromContext(securityContext);
            if (pageModel == null) {
                response = voucherService.getAllVoucherByOwnerId(accountId, null, null);
                return ResponseEntity.badRequest().body(gson.toJson(response));
            } else {
                response = voucherService.getAllVoucherByOwnerId(accountId, pageModel.getItemsPerPage(), pageModel.getPage());
            }
            return ResponseEntity.ok(gson.toJson(response));
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}
