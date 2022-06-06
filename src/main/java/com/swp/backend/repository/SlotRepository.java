package com.swp.backend.repository;

import com.swp.backend.entity.SlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlotRepository extends JpaRepository<SlotEntity, String> {
}
