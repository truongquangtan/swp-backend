package com.swp.backend.repository;

import com.swp.backend.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
    RoleEntity findRoleEntityById(int id);

    RoleEntity findRoleEntityByRoleName(String roleName);
}
