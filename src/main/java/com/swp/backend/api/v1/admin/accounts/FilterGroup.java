package com.swp.backend.api.v1.admin.accounts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FilterGroup<V> {
    private String filterName;
    private List<Filter<V>> filters;
}
