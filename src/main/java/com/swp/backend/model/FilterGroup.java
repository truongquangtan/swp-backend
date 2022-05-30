package com.swp.backend.model;

import lombok.*;

import java.util.ArrayList;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilterGroup {
    private String filterName;
    private ArrayList<Filter> filterData;
}
