package com.swp.backend.service;

import com.swp.backend.api.v1.owner.yard.request.SubYardRequest;
import com.swp.backend.api.v1.owner.yard.request.YardRequest;
import com.swp.backend.api.v1.owner.yard.response.GetYardDetailResponse;
import com.swp.backend.api.v1.owner.yard.response.GetYardResponse;
import com.swp.backend.api.v1.yard.search.YardResponse;
import com.swp.backend.entity.*;
import com.swp.backend.model.SubYardModel;
import com.swp.backend.model.YardModel;
import com.swp.backend.myrepository.YardCustomRepository;
import com.swp.backend.repository.*;
import com.swp.backend.utils.DateHelper;
import com.swp.backend.utils.TimeMappingHelper;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private FirebaseStoreService firebaseStoreService;
    private TypeYardRepository typeYardRepository;
    private TimeMappingHelper timeMappingHelper;
    private SubYardService subYardService;

    @Transactional(rollbackFor = DataAccessException.class)
    public void createNewYard(String userId, YardRequest createYardModel, MultipartFile[] images) throws DataAccessException {
        final String parentYardId = UUID.randomUUID().toString();
        YardEntity parentYard = YardEntity.builder()
                .id(parentYardId)
                .ownerId(userId)
                .name(createYardModel.getName())
                .address(createYardModel.getAddress())
                .active(true)
                .districtId(createYardModel.getDistrictId())
                .createAt(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE))
                .openAt(LocalTime.parse(createYardModel.getOpenAt()))
                .closeAt(LocalTime.parse(createYardModel.getCloseAt()))
                .slotDuration(timeMappingHelper.getTimeMapping().get(createYardModel.getSlotDuration()))
                .build();
        //Save parent yard
        yardRepository.save(parentYard);

        //Save sub-yard
        List<SubYardRequest> subYardList = createYardModel.getSubYards();

        if (subYardList != null && subYardList.size() > 0) {
            List<SubYardEntity> subYardEntityList = new ArrayList<>();
            List<SlotEntity> slotEntityList = new ArrayList<>();
            List<TypeYard> typeList = typeYardRepository.findAll();
            HashMap<String, Integer> typeMapper = new HashMap<>();
            typeList.forEach(typeYard -> {
                typeMapper.put(typeYard.getTypeName().toUpperCase(), typeYard.getId());
            });
            subYardList.forEach(subYard -> {
                String subYardId = UUID.randomUUID().toString();
                int type = typeMapper.get(subYard.getType() == 3 ? "3 VS 3" : "5 VS 5");
                SubYardEntity subYardEntity = SubYardEntity.builder()
                        .id(subYardId)
                        .name(subYard.getName())
                        .parentYard(parentYard.getId())
                        .typeYard(type)
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
        if (images != null && images.length > 0) {
            List<YardPictureEntity> listImage = Arrays.stream(images).parallel().map(image -> {
                String url = null;
                try {
                    url = firebaseStoreService.uploadFile(image);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return YardPictureEntity.builder().image(url).refId(parentYardId).build();
            }).collect(Collectors.toList());
            if (listImage.size() > 0) {
                new Thread(() -> {
                    yardPictureRepository.saveAll(listImage);
                }).start();
            }
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

    public YardEntity getYardByIdAndNotDeleted(String yardId)
    {
        return yardRepository.findYardEntityByIdAndDeleted(yardId, false);
    }

    public void updateYard(YardEntity yard) throws DataAccessException {
        yardRepository.save(yard);
    }


    public String getYardFullAddress(String yardId) {
        YardEntity yard = getYardById(yardId);
        DistrictEntity districtEntity = districtRepository.findById(yard.getDistrictId());
        String district = districtEntity.getDistrictName();
        String province = provinceRepository.findDistinctById(districtEntity.getProvinceId()).getProvinceName();
        return yard.getAddress() + ", " + district + ", " + province;
    }


    public GetYardResponse findAllYardByOwnerId(String ownerId, Integer ofSet, Integer page) throws DataAccessException {
        int maxResult = yardRepository.countAllByOwnerIdAndDeleted(ownerId, false);

        int ofSetValue = (ofSet != null && ofSet > 0) ? ofSet : 10;
        int pageValue = (page != null && page >= 1) ? page : 1;

        Pageable pagination = PageRequest.of(pageValue - 1, ofSetValue);
        List<YardEntity> result = yardRepository.findAllByOwnerIdAndDeleted(ownerId, false, pagination);

        List<YardModel> listYard = result.stream().map(yard -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            return YardModel.builder()
                    .id(yard.getId())
                    .address(yard.getAddress())
                    .name(yard.getName())
                    .openAt(yard.getOpenAt().format(formatter))
                    .closeAt(yard.getCloseAt().format(formatter))
                    .reference(yard.getReference())
                    .createdAt(yard.getCreateAt())
                    .address(yard.getAddress())
                    .isActive(yard.isActive())
                    .build();
        }).collect(Collectors.toList());

        return GetYardResponse.builder().page(pageValue).maxResult(maxResult).listYard(listYard).build();
    }

    @Transactional
    public int inactiveAllYardsOfOwner(String ownerId) {
        return yardCustomRepository.inactivateAllYardsOfOwner(ownerId);
    }

    @Transactional
    public int reactiveAllYardsOfOwner(String ownerId) {
        return yardCustomRepository.reactivateAllYardsOfOwner(ownerId);
    }

    public GetYardDetailResponse getYardDetailResponseFromYardId(String yardId)
    {
        YardEntity yardEntity = getYardByIdAndNotDeleted(yardId);
        if(yardEntity == null)
        {
            throw new RuntimeException("Can not get yard from " + yardId);
        }

        DistrictEntity districtEntity = districtRepository.findById(yardEntity.getDistrictId());

        String districtName = districtEntity.getDistrictName();
        int provinceId = districtEntity.getProvinceId();
        String provinceName = provinceRepository.findDistinctById(provinceId).getProvinceName();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        int hour = yardEntity.getSlotDuration() / 60;
        int minute = yardEntity.getSlotDuration() % 60;
        String duration = LocalTime.of(hour, minute).format(formatter);

        List<String> images = yardPictureRepository.getAllByRefId(yardId).stream().map(yardPictureEntity -> {return yardPictureEntity.getImage();}).collect(Collectors.toList());
        List<SubYardModel> subYards = subYardService.getSubYardsByBigYard(yardId);
        return GetYardDetailResponse.builder()
                .id(yardEntity.getId())
                .name(yardEntity.getName())
                .address(yardEntity.getAddress())
                .districtId(yardEntity.getDistrictId())
                .districtName(districtName)
                .provinceId(provinceId)
                .provinceName(provinceName)
                .openTime(yardEntity.getOpenAt().format(formatter))
                .closeTime(yardEntity.getCloseAt().format(formatter))
                .duration(duration)
                .images(images)
                .subYards(subYards).build();
    }
}