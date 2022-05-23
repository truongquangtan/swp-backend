package com.swp.backend.api.v1.branch;

import com.google.gson.Gson;
import com.swp.backend.entity.BranchEntity;
import com.swp.backend.service.BranchService;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;
import java.util.List;
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
            return ResponseEntity.badRequest().body(message.DATA_NOT_MATCH);
        }
        BranchRequest branchRequest = gson.fromJson(requestBody, BranchRequest.class);
        BranchEntity branchAdded = branchService.addBranch(branchRequest);
        return ResponseEntity.ok().body(gson.toJson(branchAdded));
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteBranch(@PathVariable int id){
        int branchDeleted = branchService.removeBranch(id);
        return ResponseEntity.ok().body(gson.toJson(branchDeleted));
    }
    @GetMapping("/get")
    public ResponseEntity<String> getAllBranch(){
        List<BranchEntity> branches = branchService.getAllBranch();
        return ResponseEntity.ok().body(gson.toJson(branches));
    }
}
