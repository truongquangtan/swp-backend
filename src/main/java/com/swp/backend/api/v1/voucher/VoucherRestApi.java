package com.swp.backend.api.v1.voucher;

import com.swp.backend.service.SecurityContextService;
import com.swp.backend.service.VoucherService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
@AllArgsConstructor
public class VoucherRestApi {
    private VoucherService voucherService;
    private SecurityContextService securityContextService;
    @GetMapping( "voucher/yard/{yardId}")
    public ResponseEntity<String> getVoucherAvailableOfYard(@PathVariable String yardId){
        try {

            return ResponseEntity.ok().build();
        }catch (Exception exception){
            exception.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("owner/voucher")
    public ResponseEntity<String> getVoucherAvailableOfYard(){
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            String accountId = securityContextService.extractUsernameFromContext(securityContext);

            return ResponseEntity.ok().build();
        }catch (Exception exception){
            exception.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("owner/voucher/summary")
    public ResponseEntity<String> summaryVoucher(){
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            String accountId = securityContextService.extractUsernameFromContext(securityContext);
            return ResponseEntity.ok().build();
        }catch (Exception exception){
            exception.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping( "owner/voucher")
    public ResponseEntity<String> createVoucher(){
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            String accountId = securityContextService.extractUsernameFromContext(securityContext);

            return ResponseEntity.ok().build();
        }catch (Exception exception){
            exception.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("owner/voucher")
    public ResponseEntity<String> editVoucher(){
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            String accountId = securityContextService.extractUsernameFromContext(securityContext);

            return ResponseEntity.ok().build();
        }catch (Exception exception){
            exception.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
