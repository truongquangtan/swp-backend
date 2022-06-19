package com.swp.backend.api.v1.admin.modify_account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ModifyAccountRequest {
    private String fullName;
    private String phone;

    public boolean isValid()
    {
        if(fullName == null && phone == null)
        {
            return false;
        }

        if((fullName != null && fullName.trim().equals("")) || (phone != null && phone.length() > 10))
        {
            return false;
        }
        return true;
    }
}
