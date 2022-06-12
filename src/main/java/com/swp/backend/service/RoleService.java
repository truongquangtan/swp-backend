package com.swp.backend.service;

import com.swp.backend.entity.RoleEntity;
import com.swp.backend.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RoleService {
    RoleRepository roleRepository;

    public RoleEntity getRoleById(int id) {
        return roleRepository.findRoleEntityById(id);
    }

    public RoleEntity getRoleByRoleName(String roleName) {
        return roleRepository.findRoleEntityByRoleName(roleName);
    }

    public List<RoleEntity> getAllRole() {
        return roleRepository.findAll();
    }
}
