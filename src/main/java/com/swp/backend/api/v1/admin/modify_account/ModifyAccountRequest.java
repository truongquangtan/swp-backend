package com.swp.backend.api.v1.admin.modify_account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ModifyAccountRequest {
    public static final String PHONE_REGEX = "\\d{10}";
    private String fullName;
    private String phone;

    @JsonProperty("isActive")
    private Boolean isActive;

    public boolean isValid()
    {
        if((fullName != null && fullName.trim().equals("")) || (phone != null && !phone.trim().equals("") && !phone.matches(PHONE_REGEX)))
        {
            return false;
        }
        return true;
    }
}
