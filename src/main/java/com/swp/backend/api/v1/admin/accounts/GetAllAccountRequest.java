package com.swp.backend.api.v1.admin.accounts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllAccountRequest {
    private Integer itemsPerPage;
    private Integer page;
    private Integer role;
    private String keyword;
    private String status;
    private String date;
    private List<String> sortBy;
    private String sort;
}
