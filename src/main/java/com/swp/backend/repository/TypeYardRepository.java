package com.swp.backend.repository;

import com.swp.backend.entity.TypeYard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeYardRepository extends JpaRepository<TypeYard, Integer> {
    public TypeYard getTypeYardById(int id);
}
