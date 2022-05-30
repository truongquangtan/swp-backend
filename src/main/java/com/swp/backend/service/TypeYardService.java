package com.swp.backend.service;

import com.swp.backend.customizeRepository.MyTypeYardRepository;
import com.swp.backend.model.Filter;
import com.swp.backend.model.FilterGroup;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypeYardService {
    private MyTypeYardRepository myTypeYardRepository;

    public TypeYardService(MyTypeYardRepository myTypeYardRepository) {
        this.myTypeYardRepository = myTypeYardRepository;
    }

    public FilterGroup getTypeYardFilter(){
        List<Filter> filters = myTypeYardRepository.getTypeYardFilter();
        if(filters == null){
            return null;
        }
        return FilterGroup.builder().filterName("TYPE_YARD").filterData(filters).build();
    }
}
