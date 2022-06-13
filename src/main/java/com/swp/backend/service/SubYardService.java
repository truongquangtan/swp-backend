package com.swp.backend.service;

import com.swp.backend.entity.SubYardEntity;
import com.swp.backend.model.SubYardModel;
import com.swp.backend.myrepository.SubYardCustomRepository;
import com.swp.backend.repository.SubYardRepository;
import com.swp.backend.repository.TypeYardRepository;
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
                        .build();

            } else {
                return null;
            }
        }).collect(Collectors.toList());
    }

    public boolean isActiveSubYard(String subYardId) {
        SubYardEntity subYard = subYardRepository.getSubYardEntityByIdAndActive(subYardId, true);
        return subYard != null;
    }

    public String getBigYardIdFromSubYard(String subYardId) {
        return subYardCustomRepository.getBigYardIdFromSubYard(subYardId);
    }
}
