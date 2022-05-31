package com.swp.backend.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilterGroup {
    private String filterName;
    private List<Filter> filterData;
}
