package com.swp.backend.service;

import com.swp.backend.api.v1.owner.voucher.VoucherResponse;
import com.swp.backend.constance.VoucherProperties;
import com.swp.backend.entity.VoucherEntity;
import com.swp.backend.model.VoucherModel;
import com.swp.backend.repository.VoucherRepository;
import com.swp.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class VoucherService {
    private VoucherRepository voucherRepository;

    public VoucherEntity createVoucher(VoucherModel voucher, String ownerId) throws DataAccessException {
        String voucherCode;
        do {
            voucherCode = RandomStringUtils.random(10, true, true);
        } while (voucherRepository.findVoucherEntityByVoucherCode(voucherCode) != null);
        Timestamp startDate = DateHelper.parseTimestampNonTimeAtZone(voucher.getStartDate());
        Timestamp endDate = DateHelper.parseTimestampNonTimeAtZone(voucher.getEndDate());
        endDate = DateHelper.plusMinutes(endDate, 1439);

        VoucherEntity voucherEntity = VoucherEntity.builder()
                .id(UUID.randomUUID().toString())
                .voucherCode(voucherCode)
                .createdByAccountId(ownerId)
                .startDate(startDate)
                .endDate(endDate)
                .maxQuantity(voucher.getMaxQuantity())
                .remainder(voucher.getMaxQuantity())
                .title(voucher.getTitle())
                .description(voucher.getDescription())
                .active(true)
                .status(VoucherProperties.ACTIVE)
                .type(voucher.getType())
                .createdAt(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE))
                .build();
        if (voucherEntity.getType().equalsIgnoreCase(VoucherProperties.CASH)) {
            voucherEntity.setAmountDiscount(voucher.getAmountDiscount());
        }
        if (voucherEntity.getType().equalsIgnoreCase(VoucherProperties.PERCENT)) {
            voucherEntity.setPercentDiscount(voucher.getPercentDiscount());
        }
        voucherRepository.save(voucherEntity);
        return voucherEntity;
    }

    public VoucherResponse getAllVoucherByOwnerId(String ownerId, Integer offSet, Integer page) {
        int offSetValue = offSet != null ? offSet : 10;
        int pageValue = page != null ? page : 1;
        int maxResult = voucherRepository.countAllByCreatedByAccountId(ownerId);
        if ((pageValue - 1) * offSetValue >= maxResult) {
            pageValue = 1;
        }
        Pageable pageable = PageRequest.of((pageValue - 1), offSetValue, Sort.by("createdAt").ascending());
        List<VoucherEntity> voucherResults = voucherRepository.findVoucherEntitiesByCreatedByAccountId(ownerId, pageable);
        List<VoucherModel> voucherModels = voucherResults.stream().map((this::convertVoucherModelFromVoucherEntity)).collect(Collectors.toList());
        return VoucherResponse.builder().voucher(voucherModels).maxResult(maxResult).page(pageValue).build();
    }

    public VoucherResponse getAllVoucherForYard(String ownerId, Integer offSet, Integer page) {
        int offSetValue = offSet != null ? offSet : 10;
        int pageValue = page != null ? page : 1;
        int maxResult = voucherRepository.countAllByCreatedByAccountIdAndActive(ownerId, true);
        if ((pageValue - 1) * offSetValue >= maxResult) {
            pageValue = 1;
        }
        Pageable pageable = PageRequest.of((pageValue - 1), offSetValue, Sort.by("createdAt").ascending());
        List<VoucherEntity> voucherResults = voucherRepository.findVoucherEntitiesByCreatedByAccountIdAndActive(ownerId, true, pageable);
        List<VoucherModel> voucherModels = voucherResults.stream().map((this::convertVoucherModelFromVoucherEntity)).collect(Collectors.toList());
        return VoucherResponse.builder().voucher(voucherModels).maxResult(maxResult).page(pageValue).build();
    }

    private VoucherModel convertVoucherModelFromVoucherEntity(VoucherEntity voucherEntity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss a");
        String status = voucherEntity.getStatus();
        if (!status.equalsIgnoreCase(VoucherProperties.INACTIVE)) {
            status = voucherEntity.getEndDate().before(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE)) ? VoucherProperties.EXPIRED : status;
        }

        return VoucherModel.builder()
                .id(voucherEntity.getId())
                .createdAt(voucherEntity.getCreatedAt().toLocalDateTime().format(formatter))
                .startDate(voucherEntity.getStartDate().toLocalDateTime().format(formatter))
                .endDate(voucherEntity.getEndDate().toLocalDateTime().format(formatter))
                .voucherCode(voucherEntity.getVoucherCode())
                .title(voucherEntity.getTitle())
                .description(voucherEntity.getDescription())
                .isActive(voucherEntity.isActive())
                .reference(voucherEntity.getReference())
                .remainder(voucherEntity.getRemainder())
                .type(voucherEntity.getType())
                .status(status)
                .maxQuantity(voucherEntity.getMaxQuantity())
                .createdByAccountId(voucherEntity.getCreatedByAccountId())
                .percentDiscount(voucherEntity.getPercentDiscount())
                .amountDiscount(voucherEntity.getAmountDiscount())
                .build();
    }

    public VoucherEntity updateVoucher(VoucherEntity voucher) throws DataAccessException {
        voucherRepository.save(voucher);
        return voucher;
    }

}
