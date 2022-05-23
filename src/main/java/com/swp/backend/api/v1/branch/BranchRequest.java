package com.swp.backend.api.v1.branch;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BranchRequest {
    private String address;
    private String location;
    private String branchName;
}
