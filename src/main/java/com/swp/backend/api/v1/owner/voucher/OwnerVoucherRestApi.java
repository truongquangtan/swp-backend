package com.swp.backend.api.v1.owner.voucher;

import com.google.gson.Gson;
import com.swp.backend.exception.ErrorResponse;
import com.swp.backend.model.MessageResponse;
import com.swp.backend.model.RequestPageModel;
import com.swp.backend.model.SearchModel;
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
            MessageResponse response = MessageResponse.builder().message("Created voucher success!").build();
            return ResponseEntity.ok(gson.toJson(response));
        } catch (Exception exception) {
            ErrorResponse errorResponse = ErrorResponse.builder().stack(exception.getMessage()).message("Server busy temp can't create voucher.").build();
            return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
        }
    }

    @PostMapping("vouchers/search")
    public ResponseEntity<String> searchAndFilterVoucher(@RequestBody(required = false) SearchModel search) {
        try {
            if (search == null) {
                return ResponseEntity.badRequest().body(gson.toJson(ErrorResponse.builder().message("Missing body!").build()));
            }
            SecurityContext securityContext = SecurityContextHolder.getContext();
            String ownerId = securityContextService.extractUsernameFromContext(securityContext);
            VoucherResponse searchResult = voucherService.SearchVoucherByOwnerId(ownerId, search);
            return ResponseEntity.ok().body(gson.toJson(searchResult));
        } catch (Exception exception) {
            exception.printStackTrace();
            ErrorResponse errorResponse = ErrorResponse.builder().stack(exception.getMessage()).message("Server busy temp can't search voucher.").build();
            return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
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
                return ResponseEntity.ok().body(gson.toJson(response));
            } else {
                response = voucherService.getAllVoucherByOwnerId(accountId, pageModel.getItemsPerPage(), pageModel.getPage());
            }
            return ResponseEntity.ok(gson.toJson(response));
        } catch (Exception exception) {
            ErrorResponse errorResponse = ErrorResponse.builder().stack(exception.getMessage()).message("Server busy temp can't create voucher.").build();
            return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
        }
    }
}
