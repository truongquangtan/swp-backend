package com.swp.backend.service;

import com.swp.backend.entity.YardEntity;
import com.swp.backend.repository.YardRepository;
import com.swp.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class YardService {
    private YardRepository yardRepository;

    @Transactional
    public YardEntity createNewYard(String ownerId, String yardName, String address, int districtId, LocalTime openAt, LocalTime closeAt, int slotDuration) throws DataAccessException {
        YardEntity yard = YardEntity.builder()
                .id(UUID.randomUUID().toString())
                .ownerId(ownerId)
                .name(yardName)
                .address(address)
                .districtId(districtId)
                .createAt(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE))
                .openAt(openAt)
                .closeAt(closeAt)
                .slotDuration(slotDuration).build();
        yardRepository.save(yard);
        return yard;
    }
}
