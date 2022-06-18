package com.swp.backend.api.v1.admin.accounts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Filter<V> {
    private V value;
    private String textValue;
}
