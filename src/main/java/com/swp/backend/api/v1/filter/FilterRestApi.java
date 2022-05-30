package com.swp.backend.api.v1.filter;

import com.google.gson.Gson;
import com.swp.backend.customizeRepository.MyBranchRepository;
import com.swp.backend.model.FilterGroup;
import com.swp.backend.service.BranchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "api/v1")
public class FilterRestApi {
    private BranchService branchService;
    private Gson gson;

    public FilterRestApi(BranchService branchService, Gson gson) {
        this.branchService = branchService;
        this.gson = gson;
    }

    @GetMapping("filter")
    public ResponseEntity<String> getFilter(){
        List<FilterGroup> branchFilterGroups = branchService.getBranchFilter();
        return ResponseEntity.ok(gson.toJson(branchFilterGroups));
    }
}
