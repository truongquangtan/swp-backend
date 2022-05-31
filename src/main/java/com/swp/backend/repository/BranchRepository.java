package com.swp.backend.repository;

import com.swp.backend.entity.BranchEntity;
import org.springframework.data.repository.CrudRepository;

public interface BranchRepository extends CrudRepository<BranchEntity, Integer> {
    public BranchEntity findBranchEntityByBranchName(String name);
    public int deleteBranchEntityById(int id);
}
