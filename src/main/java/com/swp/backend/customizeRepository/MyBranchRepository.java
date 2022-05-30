package com.swp.backend.customizeRepository;

import com.swp.backend.model.Filter;
import com.swp.backend.model.FilterGroup;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MyBranchRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public FilterGroup getFilterProvince(){
        Query query = entityManager.createNativeQuery("SELECT province FROM branches WHERE is_active = 'true' GROUP BY province");
        List<Object> results = query.getResultList();
        if(results.size() == 0){
            return null;
        }
        ArrayList<Filter> listFilter = new ArrayList<>(results.size());
        for (Object object : results){
            if(object instanceof String){
                String province = (String) object;
                Filter filter = Filter.builder().name(province).value(province).build();
                listFilter.add(filter);
            }
        }
        return FilterGroup.builder().filterData(listFilter).filterName("PROVINCE").build();
    }

    public FilterGroup getFilterDistrict(){
        Query query = entityManager.createNativeQuery("SELECT district FROM branches WHERE is_active = 'true' GROUP BY district");
        List results = query.getResultList();
        if(results.size() == 0){
            return null;
        }
        ArrayList<Filter> listFilter = new ArrayList<>(results.size());
        for (Object object : results){
            if(object instanceof String){
                String district = (String) object;
                Filter filter = Filter.builder().name(district).value(district).build();
                listFilter.add(filter);
            }
        }
        return FilterGroup.builder().filterData(listFilter).filterName("DISTRICT").build();
    }
}
