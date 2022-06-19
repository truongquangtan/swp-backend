package com.swp.backend.api.v1.admin.accounts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllAccountRequest {
    public static final String NAME_ASC = "+name";
    public static final String NAME_DESC = "-name";

    private int itemsPerPage;
    private int page;
    private int role;
    private String keyword;
    private String status;
    private String date;
    private String sortBy;
    private String sort;
}
