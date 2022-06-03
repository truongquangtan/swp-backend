package com.swp.backend.service;

import com.swp.backend.entity.DistrictEntity;
import com.swp.backend.repository.DistrictRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DistrictService {
    private final DistrictRepository districtRepository;

    public List<DistrictEntity> getAllDistrict() throws DataAccessException {
        return districtRepository.findAll();
    }

    public List<DistrictEntity> getAllDistrictByProvinceId(int provinceId) throws DataAccessException{
        return districtRepository.findAllByProvinceId(provinceId);
    }
}
