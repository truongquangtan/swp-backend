package com.swp.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SearchModel {
    private String keyword;
    private String sort;
    private FilterModel filter;
}
