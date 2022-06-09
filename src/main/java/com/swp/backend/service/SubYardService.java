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

    public List<SubYardModel> getSubYardsByBigYard(String bigYardId)
    {
        List<?> queriedSubYards = subYardCustomRepository.getAllSubYardByBigYard(bigYardId);

        List<SubYardModel> subYards = queriedSubYards.stream().map(subYardQueried -> {
            SubYardEntity subYardEntity = (SubYardEntity) subYardQueried;
            String typeYard = typeYardRepository.getTypeYardById(subYardEntity.getTypeYard()).getTypeName();
            return SubYardModel.builder()
                    .id(subYardEntity.getId())
                    .name(subYardEntity.getName())
                    .typeYard(typeYard)
                    .parentYard(subYardEntity.getParentYard())
                    .createAt(subYardEntity.getCreateAt())
                    .build();
        }).collect(Collectors.toList());

        return subYards;
    }
    public boolean isActiveSubYard(String subYardId)
    {
        SubYardEntity subYard = subYardRepository.getSubYardEntityByIdAndActive(subYardId, true);
        return subYard != null;
    }
    public String getBigYardIdFromSubYard(String subYardId)
    {
        return subYardCustomRepository.getBigYardIdFromSubYard(subYardId);
    }
}
