package com.swp.backend.repository;

import com.swp.backend.entity.VoucherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<VoucherEntity, String> {
    public List<VoucherEntity> findVoucherEntitiesByCreatedByAccountId(String accountId);
    public long countAllByCreatedByAccountIdAndReference(String accountId, String yardId);
    public List<VoucherEntity> findVoucherEntitiesByStartDateBeforeAndEndDateAfterAndReference(Timestamp dateBefore, Timestamp dateAfter, String yardId);
    public List<VoucherEntity> findVoucherEntitiesByStartDateBeforeAndEndDateAfterAndCreatedByAccountId(Timestamp dateBefore, Timestamp dateAfter, String accountId);
}
