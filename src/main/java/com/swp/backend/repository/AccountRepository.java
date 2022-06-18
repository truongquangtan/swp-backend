package com.swp.backend.repository;

import com.swp.backend.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, String> {
    public AccountEntity findUserEntityByEmail(String email);

    public AccountEntity findUserEntityByUserId(String userId);

    public AccountEntity findUserEntityByPhone(String phone);

    public List<AccountEntity> findAccountEntitiesByRoleIdOrRoleId(int role1, int role2);

    public int countAccountEntitiesByRoleIdOrRoleId(int role1, int role2);
}
