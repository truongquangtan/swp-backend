package com.swp.backend.repository;

import com.swp.backend.entity.YardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YardRepository extends JpaRepository<YardEntity, String> {
    public List<YardEntity> findYardEntitiesByDistrictIdAndActiveAndDeletedAndApproved(int districtIs, boolean isActive, boolean isDeleted, boolean isApproved);
}
