package com.swp.backend.api.v1.branch;

import com.google.gson.Gson;
import com.swp.backend.entity.BranchEntity;
import com.swp.backend.service.BranchService;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/branch")
public class RestBranch {
    private Gson gson;
    private BranchService branchService;
    private BranchMessageResponse message;

    public RestBranch(Gson gson, BranchService branchService){
        this.gson = gson;
        this.branchService = branchService;
    }
    @PostMapping("add")
    public ResponseEntity<String> addBranch(@RequestBody String requestBody){
        if(requestBody == null){
            return ResponseEntity.badRequest().body(message.EMPTY_BODY);
        }

        BranchRequest branchRequest = gson.fromJson(requestBody, BranchRequest.class);
        if(!branchRequest.isValidRequest()){
            return ResponseEntity.badRequest().body(message.DATA_NOT_MATCH);
        }
        try {
            BranchEntity branchAdded = branchService.addBranch(branchRequest);
            return ResponseEntity.ok().body(gson.toJson(branchAdded));
        } catch (Exception ex){
            return ResponseEntity.badRequest().body(ex.toString());
        }

    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteBranch(@PathVariable int id){
        int rowAffected;

        try {
            rowAffected = branchService.removeBranch(id);
        } catch (Exception ex){
            return ResponseEntity.internalServerError().body(ex.toString());
        }

        if(rowAffected == 0){
            return ResponseEntity.badRequest().body(message.ID_IS_NOT_EXISTED);
        }
        return ResponseEntity.ok().body(gson.toJson(message.DELETE_SUCCESSFULLY));
    }
    @GetMapping("/get")
    public ResponseEntity<String> getAllBranch(){
        List<BranchEntity> branches = branchService.getAllBranch();
        return ResponseEntity.ok().body(gson.toJson(branches));
    }
    @GetMapping("/get/{id}")
    public ResponseEntity<String> getBranchById(@PathVariable int id){
        BranchEntity branchEntity;

        try {
            branchEntity = branchService.getBranchById(id);
        } catch (NoSuchElementException noSuchElementException){
            return ResponseEntity.badRequest().body(message.NO_SUCH_ELEMENT);
        }

        return ResponseEntity.ok().body(gson.toJson(branchEntity));
    }
}
