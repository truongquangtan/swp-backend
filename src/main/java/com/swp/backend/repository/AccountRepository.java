package com.swp.backend.repository;

import com.swp.backend.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, String> {
    AccountEntity findUserEntityByEmail(String email);

    AccountEntity findUserEntityByUserId(String userId);

    AccountEntity findUserEntityByPhone(String phone);

    List<AccountEntity> findAccountEntitiesByRoleIdOrRoleId(int role1, int role2);
}
