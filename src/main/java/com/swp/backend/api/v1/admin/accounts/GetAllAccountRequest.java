package com.swp.backend.api.v1.admin.accounts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllAccountRequest {
    public static final String NAME_ASC = "+name";
    public static final String NAME_DESC = "-name";

    private Integer itemsPerPage;
    private Integer page;
    private Integer role;
    private String keyword;
    private String status;
    private String date;
    private String sortBy;
    private String sort;
}
