package com.swp.backend.api.v1.account.forgot;

import com.swp.backend.utils.RegexHelper;
import lombok.Data;

@Data
public class SendMailRequest {
    private String email;

    public boolean isValid() {
        return email != null && email.matches(RegexHelper.EMAIL_REGEX);
    }
}
