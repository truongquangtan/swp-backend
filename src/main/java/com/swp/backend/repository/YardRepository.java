package com.swp.backend.repository;

import com.swp.backend.entity.YardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YardRepository extends JpaRepository<YardEntity, String> {
}
