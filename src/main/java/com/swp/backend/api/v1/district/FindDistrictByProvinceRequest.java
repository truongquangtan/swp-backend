package com.swp.backend.api.v1.district;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindDistrictByProvinceRequest {
    private int provinceId;
}
