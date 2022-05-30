package com.swp.backend.customizeRepository;

import com.swp.backend.entity.TypeYardEntity;
import com.swp.backend.model.Filter;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class MyTypeYardRepository {
    @PersistenceContext
    EntityManager entityManager;

    public List<Filter> getTypeYardFilter(){
        Query query = entityManager.createNativeQuery("SELECT id, type_name FROM type_yards", TypeYardEntity.class);
        List<TypeYardEntity> typeYardEntityList = query.getResultList();
        List<Filter> typeFilters = typeYardEntityList.stream().map(typeYardEntity -> {
            return Filter.builder().value(String.valueOf(typeYardEntity.getTypeId())).name(typeYardEntity.getTypeName()).build();
        }).collect(Collectors.toList());
        return typeFilters;
    }

}
