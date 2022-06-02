package com.swp.backend.repository;

import com.swp.backend.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
    public RoleEntity findRoleEntityById(int id);
    public RoleEntity findRoleEntityByRoleName(String roleName);
}