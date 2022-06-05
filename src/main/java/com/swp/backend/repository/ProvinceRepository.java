package com.swp.backend.repository;

import com.swp.backend.entity.ProvinceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProvinceRepository extends JpaRepository<ProvinceEntity, Integer> {
    public List<ProvinceEntity> findAll();
    public ProvinceEntity findDistinctById(int id);
}