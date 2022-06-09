package com.swp.backend.repository;

import com.swp.backend.entity.YardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YardRepository extends JpaRepository<YardEntity, String> {
    List<YardEntity> findYardEntitiesByDistrictIdAndActiveAndDeleted(int districtId, boolean isActive, boolean isDeleted);
    YardEntity findYardEntityByIdAndActiveAndDeleted(String yardId, boolean isActive, boolean isDeleted);
}
