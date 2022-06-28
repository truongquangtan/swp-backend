package com.swp.backend.repository;

import com.swp.backend.entity.VoucherEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<VoucherEntity, String> {
    public List<VoucherEntity> findVoucherEntitiesByCreatedByAccountId(String accountId, Pageable pageable);

    public List<VoucherEntity> findVoucherEntitiesByCreatedByAccountIdAndActive(String accountId, boolean active, Pageable pageable);

    public VoucherEntity findVoucherEntityByVoucherCode(String voucherCode);

    public int countAllByCreatedByAccountId(String ownerId);

    public int countAllByCreatedByAccountIdAndActive(String ownerId, boolean active);

    public long countAllByCreatedByAccountIdAndReference(String accountId, String yardId);

    public List<VoucherEntity> findVoucherEntitiesByStartDateBeforeAndEndDateAfterAndReference(Timestamp dateBefore, Timestamp dateAfter, String yardId);

    public List<VoucherEntity> findVoucherEntitiesByStartDateBeforeAndEndDateAfterAndCreatedByAccountId(Timestamp dateBefore, Timestamp dateAfter, String accountId);
}