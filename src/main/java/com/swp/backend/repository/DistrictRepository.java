package com.swp.backend.repository;

import com.swp.backend.entity.DistrictEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepository extends JpaRepository<DistrictEntity, Integer> {
    public List<DistrictEntity> findAll();
    public DistrictEntity findDistrictEntityByDistrictName(String districtName);
    public List<DistrictEntity> findAllByProvinceId(int provinceId);
    public DistrictEntity findById(int provinceId);
}
