package com.swp.backend.api.v1.yard.search;


import com.google.gson.Gson;
import com.swp.backend.entity.YardEntity;
import com.swp.backend.service.YardService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1/yard")
public class SearchYardRestApi {

    private YardService yardService;
    private Gson gson;
    private YardResponseMemberMapping mapping;

    @PostMapping(value = "search")
    public ResponseEntity<String> searchYardByLocation(@RequestBody(required = false) SearchYardRequest searchYardRequest)
    {

        if(searchYardRequest == null){
            return ResponseEntity.badRequest().body("Null Request Body");
        }

        if(searchYardRequest.getItemsPerPage() == null)
        {
            return ResponseEntity.badRequest().body("Null Item Per Page");
        }

        if(searchYardRequest.getProvinceId() == null && searchYardRequest.getDistrictId() != null)
        {
            return ResponseEntity.badRequest().body("Province is null");
        }

        if(searchYardRequest.getProvinceId() != null && searchYardRequest.getDistrictId() == null)
        {
            return processFilterOnlyByProvice(searchYardRequest);
        }

        if(searchYardRequest.getProvinceId() != null && searchYardRequest.getProvinceId() != null)
        {
            return processFilterByDistrict(searchYardRequest);
        }


        return ResponseEntity.ok().body("Other case");
    }

    private ResponseEntity<String> processFilterOnlyByProvice(SearchYardRequest searchYardRequest)
    {
        List<YardEntity> allYards = yardService.getYardFilterByProvince(searchYardRequest.getProvinceId());
        return processResponseByPerPageFormat(allYards, searchYardRequest);
    }

    private ResponseEntity<String> processFilterByDistrict(SearchYardRequest searchYardRequest)
    {
        List<YardEntity> allYards = yardService.getYardFilterByDistrict(searchYardRequest.getDistrictId());
        return processResponseByPerPageFormat(allYards, searchYardRequest);
    }

    private ResponseEntity<String> processResponseByPerPageFormat(List<YardEntity> allYards, SearchYardRequest searchYardRequest)
    {
        int totalNumberOfYard = allYards.size();

        int itemPerPage = searchYardRequest.getItemsPerPage();
        int currentPage = searchYardRequest.getPage();

        YardResponse yardResponse = new YardResponse();

        if(itemPerPage > totalNumberOfYard)
        {
            List<YardResponseMember> responseYards = mapping.mapFromListYardEntityToListYardResponse(allYards);
            yardResponse = new YardResponse(responseYards, currentPage, totalNumberOfYard);
            return ResponseEntity.ok().body(gson.toJson(yardResponse));
        }

        int maxPage = totalNumberOfYard/itemPerPage;
        if(currentPage > maxPage)
        {
            return ResponseEntity.badRequest().body("current page bigger than max page");
        }

        List<YardResponseMember> responseYards = new ArrayList<>();
        int startIndex = itemPerPage*(currentPage - 1);
        for(int i = startIndex; i < startIndex + itemPerPage; ++i)
        {
            YardResponseMember yardResponseMember = mapping.mapFromYardEntityToYardResponseMember(allYards.get(i));
            responseYards.add(yardResponseMember);
        }
        yardResponse = new YardResponse(responseYards, currentPage, totalNumberOfYard);
        return ResponseEntity.ok().body(gson.toJson(yardResponse));
    }
}
