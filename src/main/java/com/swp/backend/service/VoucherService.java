package com.swp.backend.service;

import com.swp.backend.api.v1.owner.voucher.VoucherResponse;
import com.swp.backend.entity.VoucherEntity;
import com.swp.backend.exception.ApplyVoucherException;
import com.swp.backend.model.BookingApplyVoucherModel;
import com.swp.backend.model.BookingModel;
import com.swp.backend.model.VoucherModel;
import com.swp.backend.myrepository.SlotCustomRepository;
import com.swp.backend.repository.VoucherRepository;
import com.swp.backend.repository.YardRepository;
import com.swp.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.swp.backend.constance.VoucherProperties.*;

@Service
@AllArgsConstructor
public class VoucherService {
    private VoucherRepository voucherRepository;
    private YardRepository yardRepository;
    private SlotCustomRepository slotCustomRepository;

    public void createVoucher(VoucherModel voucher, String ownerId) throws DataAccessException {
        String voucherCode;
        do {
            voucherCode = RandomStringUtils.random(15, true, true);
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
                .usages(0)
                .title(voucher.getTitle())
                .description(voucher.getDescription())
                .active(true)
                .status(ACTIVE)
                .type(voucher.getType())
                .createdAt(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE))
                .build();
        if (voucherEntity.getType().equalsIgnoreCase(CASH)) {
            voucherEntity.setAmountDiscount(voucher.getAmountDiscount());
        }
        if (voucherEntity.getType().equalsIgnoreCase(PERCENT)) {
            voucherEntity.setPercentDiscount(voucher.getPercentDiscount());
        }
        voucherRepository.save(voucherEntity);
    }

    public VoucherResponse getAllVoucherByOwnerId(String ownerId, Integer offSet, Integer page) {
        int offSetValue = offSet != null ? offSet : 10;
        int pageValue = page != null ? page : 1;
        int maxResult = voucherRepository.countAllByCreatedByAccountId(ownerId);
        if ((pageValue - 1) * offSetValue >= maxResult) {
            pageValue = 1;
        }
        Pageable pageable = PageRequest.of((pageValue - 1), offSetValue, Sort.by("createdAt").descending());
        List<VoucherEntity> voucherResults = voucherRepository.findVoucherEntitiesByCreatedByAccountId(ownerId, pageable);
        List<VoucherModel> voucherModels = voucherResults.stream().map((this::convertVoucherModelFromVoucherEntity)).collect(Collectors.toList());
        return VoucherResponse.builder().vouchers(voucherModels).maxResult(maxResult).page(pageValue).build();
    }

    public VoucherResponse getAllVoucherForYard(String ownerId, Integer offSet, Integer page) {
        int offSetValue = offSet != null ? offSet : 10;
        int pageValue = page != null ? page : 1;
        Timestamp now = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);
        int maxResult = voucherRepository.countAllByCreatedByAccountIdAndEndDateAfterAndActive(ownerId, now, true);
        if ((pageValue - 1) * offSetValue >= maxResult) {
            pageValue = 1;
        }
        Pageable pageable = PageRequest.of((pageValue - 1), offSetValue, Sort.by("createdAt").ascending());
        List<VoucherEntity> voucherResults = voucherRepository.findVoucherEntitiesByCreatedByAccountIdAndEndDateAfterAndActive(ownerId, now, true, pageable);
        List<VoucherModel> voucherModels = voucherResults.stream().map((this::convertVoucherModelFromVoucherEntity)).collect(Collectors.toList());
        voucherModels = voucherModels.stream().filter(voucherModel -> voucherModel.getMaxQuantity() > voucherModel.getUsages()).collect(Collectors.toList());
        return VoucherResponse.builder().vouchers(voucherModels).maxResult(maxResult).page(pageValue).build();
    }

    private VoucherModel convertVoucherModelFromVoucherEntity(VoucherEntity voucherEntity) {
        String status = voucherEntity.getStatus();
        if (!status.equalsIgnoreCase(INACTIVE)) {
            status = voucherEntity.getEndDate().before(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE)) ? EXPIRED : status;
        }

        return VoucherModel.builder()
                .id(voucherEntity.getId())
                .createdAt(voucherEntity.getCreatedAt().toString())
                .startDate(voucherEntity.getStartDate().toString())
                .endDate(voucherEntity.getEndDate().toString())
                .voucherCode(voucherEntity.getVoucherCode())
                .title(voucherEntity.getTitle())
                .description(voucherEntity.getDescription())
                .reference(voucherEntity.getReference())
                .usages(voucherEntity.getUsages())
                .type(voucherEntity.getType())
                .status(status)
                .maxQuantity(voucherEntity.getMaxQuantity())
                .createdByAccountId(voucherEntity.getCreatedByAccountId())
                .percentDiscount(voucherEntity.getPercentDiscount())
                .amountDiscount(voucherEntity.getAmountDiscount())
                .build();
    }

    public void updateVoucher(VoucherModel voucher) throws DataAccessException {
        VoucherEntity voucherEntity = voucherRepository.getVoucherEntityById(voucher.getId());
        voucherEntity.setActive(voucher.getIsActive() != null ? voucher.getIsActive() : voucherEntity.isActive());
        voucherRepository.save(voucherEntity);
    }

    public List<BookingApplyVoucherModel> calculationPriceApplyVoucher(List<BookingModel> listBooking, VoucherEntity voucherApply) throws ApplyVoucherException {
        String typeVoucher = voucherApply.getType();
        if (voucherApply.getType().equalsIgnoreCase(PERCENT)) {
            return listBooking.stream().map(booking -> {
                int discountAmount = booking.getPrice() * voucherApply.getPercentDiscount() / 100;
                int newPrice = booking.getPrice() - discountAmount;
                return BookingApplyVoucherModel.builder()
                        .slotId(booking.getSlotId())
                        .date(booking.getDate())
                        .refSubYard(booking.getRefSubYard())
                        .originalPrice(booking.getPrice())
                        .price(newPrice)
                        .discountPrice(booking.getPrice() - newPrice)
                        .refSubYard(booking.getRefSubYard())
                        .build();
            }).collect(Collectors.toList());
        }
        if (typeVoucher.equalsIgnoreCase(CASH)) {
            int numberOfBooking = listBooking.size();
            int discountPerBooking = voucherApply.getAmountDiscount() / numberOfBooking;
            int remainderPercentDiscount = voucherApply.getAmountDiscount() % numberOfBooking;
            List<BookingApplyVoucherModel> bookingApplyVoucherModels = listBooking.stream().map(booking -> {
                int oldPrice = booking.getPrice();
                int newPrice = oldPrice - discountPerBooking;
                return BookingApplyVoucherModel.builder()
                        .slotId(booking.getSlotId())
                        .date(booking.getDate())
                        .refSubYard(booking.getRefSubYard())
                        .originalPrice(booking.getPrice())
                        .price(Math.max(newPrice, 0))
                        .discountPrice(Math.min(oldPrice, discountPerBooking))
                        .refSubYard(booking.getRefSubYard())
                        .build();
            }).collect(Collectors.toList());

            if(remainderPercentDiscount != 0){
                BookingApplyVoucherModel lastBooking = bookingApplyVoucherModels.get(bookingApplyVoucherModels.size() - 1);
                int lastBookingDiscountPrice = lastBooking.getPrice() + remainderPercentDiscount;
                lastBooking.setPrice(lastBookingDiscountPrice);
                lastBooking.setDiscountPrice(lastBooking.getDiscountPrice() + remainderPercentDiscount);
            }
            return bookingApplyVoucherModels;
        }
        return null;
    }

    public VoucherEntity getValidVoucherByVoucherCodeAndSlotId(String voucherCode, int slotId) throws ApplyVoucherException {
        if (voucherCode == null || voucherCode.trim().length() == 0) {
            throw ApplyVoucherException.builder().errorMessage("Voucher code is not valid.").build();
        }
        VoucherEntity voucherApply = voucherRepository.findVoucherEntityByVoucherCode(voucherCode);
        if (voucherApply == null || !voucherApply.getStatus().equalsIgnoreCase(ACTIVE)) {
            throw ApplyVoucherException.builder().errorMessage("Voucher is not available.").build();
        }

        if (voucherApply.getMaxQuantity() <= voucherApply.getUsages()) {
            throw ApplyVoucherException.builder().errorMessage("Voucher it's over").build();
        }
        String ownerId = slotCustomRepository.findOwnerIdFromSlotId(slotId);
        if (ownerId == null || !ownerId.equalsIgnoreCase(voucherApply.getCreatedByAccountId())) {
            throw ApplyVoucherException.builder().errorMessage("Voucher isn't apply for this yard.").build();
        }

        return voucherApply;
    }
}
