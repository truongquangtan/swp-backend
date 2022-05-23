package com.swp.backend.api.v1.branch;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BranchRequest {
    private String address;
    private String location;
    private String branchName;

    public boolean isValidRequest() {
        if(address == null || location == null || branchName == null) {
            return false;
        }
        return address.trim() != "" && location.trim() != "" && branchName.trim() != "";
    }
}
