package com.swp.backend.service;

import com.swp.backend.api.v1.sub_yard.get_by_owner.GetSubYardDetailResponse;
import com.swp.backend.entity.SlotEntity;
import com.swp.backend.entity.SubYardEntity;
import com.swp.backend.entity.YardEntity;
import com.swp.backend.model.Slot;
import com.swp.backend.model.SubYardModel;
import com.swp.backend.model.model_builder.ListSlotBuilder;
import com.swp.backend.myrepository.SubYardCustomRepository;
import com.swp.backend.repository.SlotRepository;
import com.swp.backend.repository.SubYardRepository;
import com.swp.backend.repository.TypeYardRepository;
import com.swp.backend.repository.YardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SubYardService {
    private SubYardCustomRepository subYardCustomRepository;
    private SubYardRepository subYardRepository;
    private TypeYardRepository typeYardRepository;
    private SlotRepository slotRepository;
    private YardRepository yardRepository;

    private List<SubYardEntity> findSubYardByParentId(String bigYardId) {
        return subYardCustomRepository.getAllSubYardByBigYard(bigYardId);
    }

    public List<SubYardModel> getSubYardsByBigYard(String bigYardId) {
        List<?> queriedSubYards = findSubYardByParentId(bigYardId);

        return queriedSubYards.stream().map(object -> {
            if (object instanceof SubYardEntity) {
                SubYardEntity subYardEntity = (SubYardEntity) object;
                String typeYard = typeYardRepository.getTypeYardById(subYardEntity.getTypeYard()).getTypeName();
                return SubYardModel.builder()
                        .id(subYardEntity.getId())
                        .name(subYardEntity.getName())
                        .typeYard(typeYard)
                        .parentYard(subYardEntity.getParentYard())
                        .createAt(subYardEntity.getCreateAt())
                        .isActive(true)
                        .build();

            } else {
                return null;
            }
        }).collect(Collectors.toList());
    }

    public GetSubYardDetailResponse getSubYardDetailResponse(String ownerId, String yardId, String subYardId) {
        YardEntity yardEntity = yardRepository.findYardEntityByIdAndDeleted(yardId, false);

        if (yardEntity == null) {
            throw new RuntimeException("The yard is deleted or not existed");
        }

        if (!yardEntity.getOwnerId().equals(ownerId)) {
            throw new RuntimeException("The owner is not author of this yard.");
        }

        if (!getBigYardIdFromSubYard(subYardId).equals(yardId)) {
            throw new RuntimeException("The sub-yard is not in this yard");
        }

        return processGetSubYardDetailResponseByOwner(subYardId);
    }

    public GetSubYardDetailResponse processGetSubYardDetailResponseByOwner(String subYardId) {
        SubYardEntity subYardEntity = subYardRepository.getSubYardEntitiesById(subYardId);
        String type = typeYardRepository.getTypeYardById(subYardEntity.getTypeYard()).getTypeName();
        List<SlotEntity> slotEntities = slotRepository.findSlotEntitiesByRefYard(subYardId);
        List<Slot> slots = ListSlotBuilder.getAvailableSlotsFromSlotEntities(slotEntities);


        return GetSubYardDetailResponse.builder()
                .type(type)
                .name(subYardEntity.getName())
                .isActive(subYardEntity.isActive())
                .slots(slots)
                .build();
    }

    public boolean isActiveSubYard(String subYardId) {
        SubYardEntity subYard = subYardRepository.getSubYardEntityByIdAndActive(subYardId, true);
        return subYard != null;
    }

    public String getBigYardIdFromSubYard(String subYardId) {
        return subYardCustomRepository.getBigYardIdFromSubYard(subYardId);
    }
}
