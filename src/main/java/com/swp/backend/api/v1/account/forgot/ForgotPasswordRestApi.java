package com.swp.backend.api.v1.account.forgot;

import com.swp.backend.entity.AccountEntity;
import com.swp.backend.service.AccountService;
import com.swp.backend.service.OtpStateService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/forgot")
public class ForgotPasswordRestApi {
    private OtpStateService otpStateService;
    private AccountService accountService;

    @PostMapping(value = "sendmail")
    public ResponseEntity<String> sendOtpRestPassword(@RequestBody(required = false) SendMailRequest sendMailRequest){
        if(sendMailRequest == null){
            return ResponseEntity.badRequest().body("Missing body");
        }

        AccountEntity account = accountService.findAccountByUsername(sendMailRequest.getEmail());
        if(account == null){
            return ResponseEntity.badRequest().body("Email incorrect.");
        }
        otpStateService.sendEmailOtpRestPassword(account.getUserId(), sendMailRequest.getEmail());
        return ResponseEntity.ok().body("Send email reset password success!");
    }

    @PostMapping(name = "confirm-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody(required = false) VerifyOtpRequest verifyOtpRequest){
        if(verifyOtpRequest == null){
            return ResponseEntity.badRequest().body("Missing body.");
        }
        return ResponseEntity.ok().build();
    }

}
