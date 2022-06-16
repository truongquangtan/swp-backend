package com.swp.backend.service;

import com.swp.backend.api.v1.owner.yard.SubYardRequest;
import com.swp.backend.api.v1.owner.yard.YardRequest;
import com.swp.backend.api.v1.yard.search.YardResponse;
import com.swp.backend.entity.*;
import com.swp.backend.model.YardModel;
import com.swp.backend.myrepository.YardCustomRepository;
import com.swp.backend.repository.*;
import com.swp.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class YardService {
    private YardRepository yardRepository;
    private SlotRepository slotRepository;
    private SubYardRepository subYardRepository;
    private DistrictRepository districtRepository;
    private ProvinceRepository provinceRepository;

    private YardPictureRepository yardPictureRepository;
    private YardCustomRepository yardCustomRepository;

    @Transactional(rollbackFor = DataAccessException.class)
    public void createNewYard(String userId, YardRequest yardRequest) throws DataAccessException {
        YardEntity parentYard = YardEntity.builder()
                .id(UUID.randomUUID().toString())
                .ownerId(userId)
                .name(yardRequest.getName())
                .address(yardRequest.getAddress())
                .active(true)
                .districtId(yardRequest.getDistrictId())
                .createAt(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE))
                .openAt(LocalTime.parse(yardRequest.getOpenAt()))
                .closeAt(LocalTime.parse(yardRequest.getCloseAt()))
                .slotDuration(Integer.parseInt(yardRequest.getSlotDuration()))
                .build();
        //Save parent yard
        yardRepository.save(parentYard);
        //Save sub-yard
        List<SubYardRequest> subYardList = yardRequest.getSubYards();
        if (subYardList != null && subYardList.size() > 0) {
            List<SubYardEntity> subYardEntityList = new ArrayList<>();
            List<SlotEntity> slotEntityList = new ArrayList<>();
            subYardList.forEach(subYard -> {
                String subYardId = UUID.randomUUID().toString();
                SubYardEntity subYardEntity = SubYardEntity.builder()
                        .id(subYardId)
                        .name(subYard.getName())
                        .parentYard(parentYard.getId())
                        .typeYard(Integer.parseInt(subYard.getType()))
                        .active(true)
                        .createAt(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE))
                        .build();
                subYardEntityList.add(subYardEntity);
                subYard.getSlots().forEach(slot -> {
                    SlotEntity slotEntity = SlotEntity.builder()
                            .refYard(subYardId)
                            .startTime(LocalTime.parse(slot.getStartTime()))
                            .endTime(LocalTime.parse(slot.getEndTime()))
                            .active(true)
                            .price(slot.getPrice())
                            .build();
                    slotEntityList.add(slotEntity);
                });
            });
            subYardRepository.saveAll(subYardEntityList);
            slotRepository.saveAll(slotEntityList);
        }
    }

    public YardResponse findYardByFilter(Integer provinceId, Integer districtId, Integer ofSet, Integer page) {
        int pageValue = (page == null || page < 1) ? 1 : page;
        int ofSetValue = (ofSet == null || ofSet < 1) ? 6 : ofSet;

        int maxResult = yardCustomRepository.getMaxResultFindYardByFilter(provinceId, districtId);
        if (((pageValue - 1) * ofSetValue) >= maxResult) {
            pageValue = 1;
        }
        List<?> listResult = yardCustomRepository.findYardByFilter(provinceId, districtId, ofSetValue, pageValue);
        List<YardModel> yardModels = listResult.stream().map(item -> {
            if (item instanceof YardEntity) {
                YardEntity yard = (YardEntity) item;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                DistrictEntity districtEntity = districtRepository.findById(yard.getDistrictId());
                String province = provinceRepository.findDistinctById(districtEntity.getProvinceId()).getProvinceName();
                return YardModel.builder()
                        .id(yard.getId())
                        .name(yard.getName())
                        .address(yard.getAddress())
                        .districtName(districtRepository.findById(yard.getDistrictId()).getDistrictName())
                        .province(province)
                        .openAt(yard.getOpenAt().format(formatter))
                        .closeAt(yard.getCloseAt().format(formatter))
                        .reference(yard.getReference())
                        .build();
            } else {
                return null;
            }
        }).collect(Collectors.toList());

        yardModels.forEach(yardModel -> {
            List<String> images = new ArrayList<>();
            List<YardPictureEntity> listPicture = yardPictureRepository.getAllByRefId(yardModel.getId());
            listPicture.forEach(picture -> images.add(picture.getImage()));
            yardModel.setImages(images);
        });
        return YardResponse.builder().yards(yardModels).maxResult(maxResult).page(pageValue).build();
    }

    public boolean isAvailableYard(String yardId) {
        YardEntity yard = yardRepository.findYardEntityByIdAndActiveAndDeleted(yardId, true, false);
        return yard != null;
    }

    public YardModel getYardModelFromYardId(String yardId) {
        YardEntity yard = yardRepository.findYardEntityByIdAndActiveAndDeleted(yardId, true, false);
        if (yard == null) {
            return null;
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            int provinceId = districtRepository.findById(yard.getDistrictId()).getProvinceId();
            String provinceName = provinceRepository.findDistinctById(provinceId).getProvinceName();
            YardModel yardModel = YardModel.builder()
                    .id(yard.getId())
                    .name(yard.getName())
                    .address(yard.getAddress())
                    .districtName(districtRepository.findById(yard.getDistrictId()).getDistrictName())
                    .province(provinceName)
                    .openAt(yard.getOpenAt().format(formatter))
                    .closeAt(yard.getCloseAt().format(formatter))
                    .build();
            List<String> images = new ArrayList<>();
            List<YardPictureEntity> listPicture = yardPictureRepository.getAllByRefId(yardModel.getId());
            listPicture.forEach(picture -> images.add(picture.getImage()));
            yardModel.setImages(images);
            return yardModel;
        }
    }

    public YardEntity getYardById(String yardId) {
        return yardRepository.findYardEntitiesById(yardId);
    }

    public void updateYard(YardEntity yard) throws DataAccessException {
        yardRepository.save(yard);
    }

    public String getYardFullAddress(String yardId)
    {
        YardEntity yard = getYardById(yardId);
        DistrictEntity districtEntity = districtRepository.findById(yard.getDistrictId());
        String district = districtEntity.getDistrictName();
        String province = provinceRepository.findDistinctById(districtEntity.getProvinceId()).getProvinceName();
        return yard.getAddress() + ", " + district + ", " + province;
    }
}