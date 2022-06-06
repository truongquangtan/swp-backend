package com.swp.backend.service;

import com.swp.backend.entity.ProvinceEntity;
import com.swp.backend.repository.ProvinceRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProvinceService {
    private final ProvinceRepository provinceRepository;

    public List<ProvinceEntity> getAllProvince() throws DataAccessException {
        return provinceRepository.findAll();
    }
}
