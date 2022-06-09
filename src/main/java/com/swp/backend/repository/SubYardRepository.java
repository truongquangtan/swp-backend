package com.swp.backend.repository;

import com.swp.backend.entity.SubYardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubYardRepository extends JpaRepository<SubYardEntity, String> {
    SubYardEntity getSubYardEntityByIdAndActive(String id, boolean isActive);
}
