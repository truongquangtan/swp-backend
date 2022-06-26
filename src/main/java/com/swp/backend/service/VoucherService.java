package com.swp.backend.service;

import com.swp.backend.entity.VoucherEntity;
import com.swp.backend.repository.VoucherRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VoucherService {
    private VoucherRepository voucherRepository;

    public VoucherEntity createVoucher(VoucherEntity voucher) throws DataAccessException{
        voucherRepository.save(voucher);
        return voucher;
    }

    public VoucherEntity updateVoucher(VoucherEntity voucher) throws DataAccessException{
        voucherRepository.save(voucher);
        return voucher;
    }

}
