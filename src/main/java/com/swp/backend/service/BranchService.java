package com.swp.backend.service;

import com.swp.backend.api.v1.branch.BranchNameExistedException;
import com.swp.backend.api.v1.branch.BranchRequest;
import com.swp.backend.customizeRepository.MyBranchRepository;
import com.swp.backend.entity.BranchEntity;
import com.swp.backend.model.Filter;
import com.swp.backend.model.FilterGroup;
import com.swp.backend.repository.BranchRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Transactional
public class BranchService {
    private final BranchRepository branchRepository;
    private final MyBranchRepository myBranchRepository;


    public BranchService(BranchRepository branchRepository, MyBranchRepository myBranchRepository) {
        this.branchRepository = branchRepository;
        this.myBranchRepository = myBranchRepository;
    }

    public BranchEntity addBranch(BranchRequest branchRequest) throws BranchNameExistedException {
        if (isBranchNameExisted(branchRequest.getBranchName())) {
            throw new BranchNameExistedException();
        }
        BranchEntity branchAdded = MapBranchEntityFromBranchRequest(branchRequest);
        try {
            branchAdded = branchRepository.save(branchAdded);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return branchAdded;
    }
    private BranchEntity MapBranchEntityFromBranchRequest(BranchRequest branchRequest){
        BranchEntity branchEntity = new BranchEntity();
        branchEntity.setBranchName(branchRequest.getBranchName());
        branchEntity.setAddress(branchRequest.getAddress());
        branchEntity.setAddress(branchRequest.getLocation());
        return branchEntity;
    }
    private boolean isBranchNameExisted(String name){
        return branchRepository.findBranchEntityByBranchName(name) != null;
    }

    public int removeBranch(int id) {
        int branchDelete = branchRepository.deleteBranchEntityById(id);
        return branchDelete;
    }
    public List<BranchEntity> getAllBranch(){
        List<BranchEntity> result = new ArrayList<BranchEntity>();

        Iterator iterator = branchRepository.findAll().iterator();
        while(iterator.hasNext()){
            result.add((BranchEntity) iterator.next());
        }

        return result;
    }
    public BranchEntity getBranchById(Integer id){
        BranchEntity branchEntity = branchRepository.findById(id).get();
        return branchEntity;
    }

    public List<FilterGroup> getBranchFilter(){
        FilterGroup district = myBranchRepository.getFilterDistrict();
        FilterGroup province = myBranchRepository.getFilterProvince();
        if(district == null && province == null){
            return null;
        }
        ArrayList<FilterGroup> filterGroups = new ArrayList<>();
        if(district != null){
            filterGroups.add(district);
        }

        if(province != null){
            filterGroups.add(province);
        }
        return filterGroups;
    }
}
