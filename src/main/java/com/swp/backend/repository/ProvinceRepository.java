package com.swp.backend.repository;

import com.swp.backend.entity.ProvinceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvinceRepository extends JpaRepository<ProvinceEntity, Integer> {
    public ProvinceEntity findDistinctById(int id);
}
