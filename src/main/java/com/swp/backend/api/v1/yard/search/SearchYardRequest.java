package com.swp.backend.api.v1.yard.search;

import lombok.Data;

@Data
public class SearchYardRequest {
    private Integer provinceId;
    private Integer districtId;
    private Integer itemPerPage;
    private Integer currentPage = 1;
}
