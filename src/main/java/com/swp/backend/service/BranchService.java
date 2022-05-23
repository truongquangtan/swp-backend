package com.swp.backend.service;

import com.swp.backend.api.v1.branch.BranchNameExistedException;
import com.swp.backend.api.v1.branch.BranchRequest;
import com.swp.backend.entity.BranchEntity;
import com.swp.backend.repository.BranchRepository;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class BranchService {
    private final BranchRepository branchRepository;

    public BranchService(BranchRepository branchRepository){
        this.branchRepository = branchRepository;
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
        branchEntity.setLocation(branchRequest.getLocation());
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
}
