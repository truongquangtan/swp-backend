package com.swp.backend.service;

import com.swp.backend.api.v1.owner.voucher.VoucherResponse;
import com.swp.backend.entity.VoucherEntity;
import com.swp.backend.exception.ApplyVoucherException;
import com.swp.backend.model.*;
import com.swp.backend.myrepository.SlotCustomRepository;
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
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.swp.backend.constance.VoucherProperties.*;

@Service
@AllArgsConstructor
public class VoucherService {
    private VoucherRepository voucherRepository;
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
                .discount(voucher.getDiscount())
                .active(true)
                .status(ACTIVE)
                .type(voucher.getType())
                .createdAt(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE))
                .build();
        voucherRepository.save(voucherEntity);
    }

    public VoucherResponse SearchVoucherByOwnerId(String ownerId, SearchModel searchModel) {
        List<VoucherEntity> voucherResults = findAllVoucherByOwnerId(ownerId, null);
        voucherResults = handleSearchByKeyword(searchModel.getKeyword(), voucherResults);
        voucherResults = handleFilterVoucher(searchModel.getFilter(), voucherResults);
        voucherResults = handleSortByColumn(searchModel.getSort(), voucherResults);

        if (voucherResults.size() == 0) {
            return VoucherResponse.builder().page(1).maxResult(0).message("Did not any result matches with keyword. Try again!").build();
        }
        List<VoucherModel> voucherModels = voucherResults.stream().map((this::convertVoucherModelFromVoucherEntity)).collect(Collectors.toList());
        return VoucherResponse.builder().vouchers(voucherModels).maxResult(voucherModels.size()).page(1).build();
    }

    private List<VoucherEntity> handleFilterVoucher(FilterModel filter, List<VoucherEntity> vouchers) {
        if (filter == null || filter.getField() == null || filter.getValue() == null) {
            return vouchers;
        }

        String field = filter.getField();
        String value = filter.getValue();
        switch (field) {
            case "status":
                if (value.equalsIgnoreCase(ACTIVE)) {
                    return vouchers.stream().filter(voucher -> voucher.getStatus().equals(ACTIVE)).collect(Collectors.toList());
                }
                if (value.equalsIgnoreCase(INACTIVE)) {
                    return vouchers.stream().filter(voucher -> !voucher.isActive()).collect(Collectors.toList());
                }
                if (value.equalsIgnoreCase(EXPIRED)) {
                    return vouchers.stream().filter(voucher -> voucher.getUsages() >= voucher.getMaxQuantity() || voucher.getEndDate().compareTo(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE)) > 0).collect(Collectors.toList());
                }
                break;
            case "type":
                if (value.equalsIgnoreCase(PERCENT)) {
                    return vouchers.stream().filter(voucher -> voucher.getType().equalsIgnoreCase(PERCENT)).collect(Collectors.toList());
                }
                if (value.equalsIgnoreCase(CASH)) {
                    return vouchers.stream().filter(voucher -> voucher.getType().equalsIgnoreCase(CASH)).collect(Collectors.toList());
                }
                break;
        }
        return vouchers;
    }

    private List<VoucherEntity> handleSearchByKeyword(String searchKeyword, List<VoucherEntity> vouchers) {
        String keyword = searchKeyword != null ? searchKeyword.trim().toLowerCase() : null;
        if (vouchers == null || vouchers.size() == 0 || keyword == null || keyword.length() == 0) {
            return vouchers;
        }

        return vouchers.stream().filter(voucher -> voucher.getTitle().toLowerCase().contains(keyword)
                || voucher.getVoucherCode().toLowerCase().contains(keyword)
                || String.valueOf(voucher.getReference()).contains(keyword)
                || String.valueOf(voucher.getDiscount()).contains(keyword)).collect(Collectors.toList());
    }

    private List<VoucherEntity> handleSortByColumn(String sort, List<VoucherEntity> vouchers) {
        if (sort == null || sort.trim().length() == 0) {
            return vouchers;
        }
        char orderBy = sort.charAt(0);
        String columnSort = sort;
        if (orderBy == '+' || orderBy == '-') {
            columnSort = sort.substring(1);
        } else {
            orderBy = '+';
        }

        switch (columnSort) {
            case "code":
                if (orderBy == '-') {
                    vouchers.sort((firstVoucher, secondVoucher) -> secondVoucher.getVoucherCode().compareTo(firstVoucher.getVoucherCode()));
                } else {
                    vouchers.sort((firstVoucher, secondVoucher) -> secondVoucher.getVoucherCode().compareTo(firstVoucher.getVoucherCode()));
                }
                break;
            case "ref":
                if (orderBy == '-') {
                    vouchers.sort(Comparator.comparingInt(VoucherEntity::getReference));
                } else {
                    vouchers.sort((firstVoucher, secondVoucher) -> Integer.compare(secondVoucher.getReference(), firstVoucher.getReference()));
                }
                break;
            case "startDate":
                if (orderBy == '-') {
                    vouchers.sort(Comparator.comparing(VoucherEntity::getStartDate));
                } else {
                    vouchers.sort((firstVoucher, secondVoucher) -> secondVoucher.getStartDate().compareTo(firstVoucher.getStartDate()));
                }
                break;
            case "endDate":
                if (orderBy == '-') {
                    vouchers.sort(Comparator.comparing(VoucherEntity::getEndDate));
                } else {
                    vouchers.sort((firstVoucher, secondVoucher) -> secondVoucher.getEndDate().compareTo(firstVoucher.getEndDate()));
                }
                break;
            case "amount":
                if (orderBy == '-') {
                    vouchers.sort((firstVoucher, secondVoucher) -> Float.compare(secondVoucher.getDiscount(), firstVoucher.getDiscount()));
                } else {
                    vouchers.sort((firstVoucher, secondVoucher) -> Float.compare(firstVoucher.getDiscount(), secondVoucher.getDiscount()));
                }
                break;
        }

        return vouchers;
    }

    public VoucherResponse getAllVoucherByOwnerId(String ownerId, Integer offSet, Integer page) {
        int offSetValue = offSet != null ? offSet : 10;
        int pageValue = page != null ? page : 1;
        int maxResult = voucherRepository.countAllByCreatedByAccountId(ownerId);
        if ((pageValue - 1) * offSetValue >= maxResult) {
            pageValue = 1;
        }
        Pageable pageable = PageRequest.of((pageValue - 1), offSetValue, Sort.by("createdAt").descending());
        List<VoucherEntity> voucherResults = findAllVoucherByOwnerId(ownerId, pageable);
        List<VoucherModel> voucherModels = voucherResults.stream().map((this::convertVoucherModelFromVoucherEntity)).collect(Collectors.toList());
        return VoucherResponse.builder().vouchers(voucherModels).maxResult(maxResult).page(pageValue).build();
    }

    private List<VoucherEntity> findAllVoucherByOwnerId(String ownerId, Pageable pageable) {
        List<VoucherEntity> vouchers;
        if (pageable == null) {
            vouchers = voucherRepository.findVoucherEntitiesByCreatedByAccountId(ownerId);
        } else {
            vouchers = voucherRepository.findVoucherEntitiesByCreatedByAccountId(ownerId, pageable);
        }
        return vouchers.stream().peek(voucher -> {
            if (voucher.getUsages() >= voucher.getMaxQuantity() || voucher.getEndDate().compareTo(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE)) < 0) {
                voucher.setStatus(EXPIRED);
                voucherRepository.save(voucher);
            }
        }).collect(Collectors.toList());
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        if (!status.equalsIgnoreCase(INACTIVE)) {
            status = voucherEntity.getEndDate().before(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE)) ? EXPIRED : status;
        }

        return VoucherModel.builder()
                .id(voucherEntity.getId())
                .createdAt(voucherEntity.getCreatedAt().toString())
                .startDate(dateFormat.format(voucherEntity.getStartDate()))
                .endDate(dateFormat.format(voucherEntity.getEndDate()))
                .voucherCode(voucherEntity.getVoucherCode())
                .title(voucherEntity.getTitle())
                .description(voucherEntity.getDescription())
                .reference(voucherEntity.getReference())
                .usages(voucherEntity.getUsages())
                .type(voucherEntity.getType())
                .status(status)
                .maxQuantity(voucherEntity.getMaxQuantity())
                .createdByAccountId(voucherEntity.getCreatedByAccountId())
                .discount(voucherEntity.getDiscount())
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
                int discountAmount = Math.round(booking.getPrice() * voucherApply.getDiscount() / 100);
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
            int discountPerBooking = (int) voucherApply.getDiscount() / numberOfBooking;
            int remainderPercentDiscount = (int) voucherApply.getDiscount() % numberOfBooking;
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

            if (remainderPercentDiscount != 0) {
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

        if (voucherApply.getEndDate().compareTo(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE)) < 0) {
            voucherApply.setStatus(EXPIRED);
            voucherRepository.save(voucherApply);
            throw ApplyVoucherException.builder().errorMessage("Voucher is out of date.").build();
        }

        if (voucherApply.getMaxQuantity() <= voucherApply.getUsages()) {
            voucherApply.setStatus(EXPIRED);
            voucherRepository.save(voucherApply);
            throw ApplyVoucherException.builder().errorMessage("Voucher it's over").build();
        }
        String ownerId = slotCustomRepository.findOwnerIdFromSlotId(slotId);
        if (ownerId == null || !ownerId.equalsIgnoreCase(voucherApply.getCreatedByAccountId())) {
            throw ApplyVoucherException.builder().errorMessage("Voucher isn't apply for this yard.").build();
        }

        return voucherApply;
    }

    public VoucherEntity getValidApplyVoucherForBookingByVoucherCode(String voucherCode) {
        if (voucherCode == null || voucherCode.trim().length() == 0) {
            return null;
        }
        VoucherEntity voucher = voucherRepository.findVoucherEntityByVoucherCode(voucherCode.trim());

        if (voucher == null) {
            return null;
        }

        if (!voucher.getStatus().equals(ACTIVE)) {
            return null;
        }

        if (voucher.getMaxQuantity() - voucher.getUsages() <= 0) {
            voucher.setStatus(EXPIRED);
            voucherRepository.save(voucher);
            return null;
        }
        return voucher;
    }

    public void updateUsesVoucher(VoucherEntity voucher) {
        voucherRepository.save(voucher);
    }
}
