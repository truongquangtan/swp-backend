package com.swp.backend.api.v1.filter;

import com.google.gson.Gson;
import com.swp.backend.model.FilterGroup;
import com.swp.backend.service.BranchService;
import com.swp.backend.service.TypeYardService;
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
    private TypeYardService typeYardService;
    private Gson gson;

    public FilterRestApi(BranchService branchService, Gson gson, TypeYardService typeYardService) {
        this.branchService = branchService;
        this.gson = gson;
        this.typeYardService = typeYardService;
    }

    @GetMapping("filter")
    public ResponseEntity<String> getFilter(){
        List<FilterGroup> branchFilterGroups = branchService.getBranchFilter();
        FilterGroup typeYardFilterGroup = typeYardService.getTypeYardFilter();
        if(branchFilterGroups == null && typeYardFilterGroup == null){
            return ResponseEntity.ok().body("Filter not yet.");
        }
        List<FilterGroup> filterGroups = new ArrayList<>();
        if(branchFilterGroups!= null){
            filterGroups.addAll(branchFilterGroups);
        }
        if(typeYardFilterGroup != null){
            filterGroups.add(typeYardFilterGroup);
        }

        return ResponseEntity.ok(gson.toJson(filterGroups));
    }
}
