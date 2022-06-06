package com.swp.backend.service;

import com.swp.backend.api.v1.yard.add.SubYardRequest;
import com.swp.backend.api.v1.yard.add.YardRequest;
import com.swp.backend.api.v1.yard.search.YardResponseMember;
import com.swp.backend.entity.*;
import com.swp.backend.repository.*;
import com.swp.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class YardService {
    private YardRepository yardRepository;
    private SlotRepository slotRepository;
    private SubYardRepository subYardRepository;
    private DistrictRepository districtRepository;
    private YardPictureRepository yardPictureRepository;

    @Transactional(rollbackFor = DataAccessException.class)
    public void createNewYard(String userId, YardRequest yardRequest) throws DataAccessException {
        YardEntity parentYard = YardEntity.builder()
                .id(UUID.randomUUID().toString())
                .ownerId(userId)
                .name(yardRequest.getName())
                .address(yardRequest.getAddress())
                .districtId(yardRequest.getDistrictId())
                .createAt(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE))
                .openAt(LocalTime.parse(yardRequest.getOpenAt()))
                .closeAt(LocalTime.parse(yardRequest.getCloseAt()))
                .slotDuration(yardRequest.getSlotDuration())
                .build();
        //Save parent yard
        yardRepository.save(parentYard);
        //Save sub-yard
        List<SubYardRequest> subYardList = yardRequest.getSubYards();
        if(subYardList != null && subYardList.size() > 0){
            List<SubYardEntity> subYardEntityList = new ArrayList<>();
            List<SlotEntity> slotEntityList = new ArrayList<>();
            subYardList.forEach(subYard -> {
                String subYardId = UUID.randomUUID().toString();
                SubYardEntity subYardEntity = SubYardEntity.builder()
                        .id(subYardId)
                        .name(subYard.getName())
                        .parentYard(parentYard.getId())
                        .typeYard(Integer.parseInt(subYard.getType()))
                        .createAt(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE))
                        .build();
                subYardEntityList.add(subYardEntity);
                subYard.getSlots().forEach(slot -> {
                    SlotEntity slotEntity = SlotEntity.builder()
                            .refYard(subYardId)
                            .startTime(LocalTime.parse(slot.getStartTime()))
                            .endTime(LocalTime.parse(slot.getEndTime()))
                            .isActive(true)
                            .price(slot.getPrice())
                            .build();
                    slotEntityList.add(slotEntity);
                });
            });
            subYardRepository.saveAll(subYardEntityList);
            slotRepository.saveAll(slotEntityList);
        }
    }
    public List<YardEntity> getYardFilterByDistrict(int districtId)
    {
        return yardRepository.findYardEntitiesByDistrictIdAndActiveAndDeleted(districtId, true, false);
    }

    public List<YardEntity> getYardFilterByProvince(int provinceId)
    {
        List<DistrictEntity> districts = districtRepository.findAllByProvinceId(provinceId);
        List<YardEntity> result = new ArrayList<YardEntity>();

        districts.forEach(district -> {
            List<YardEntity> yardsPerDistrict = getYardFilterByDistrict(district.getId());
            result.addAll(yardsPerDistrict);
        });

        return result;
    }

    public YardResponseMember loadAllImages(YardResponseMember yardResponseMember)
    {
        List<YardPictureEntity> allImages = yardPictureRepository.getAllByRefId(yardResponseMember.getId());
        if(allImages == null)
        {
            return yardResponseMember;
        }
        List<String> allImagesUrl = new ArrayList<>();
        allImages.forEach(image -> {
            allImagesUrl.add(image.getImage());
        });

        yardResponseMember.setImages(allImagesUrl);
        return yardResponseMember;
    }
}