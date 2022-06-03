package com.swp.backend.api.v1.district;

import com.google.gson.Gson;
import com.swp.backend.entity.DistrictEntity;
import com.swp.backend.service.DistrictService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1")
public class DistrictRestAPI {
    private DistrictService districtService;
    private Gson gson;

    @GetMapping(value = "district")
    public ResponseEntity<String> getAllDistrict(){
        try {
            List<DistrictEntity> listDistrict = districtService.getAllDistrict();
            return ResponseEntity.ok(gson.toJson(listDistrict));
        }catch (DataAccessException accessException){
            return ResponseEntity.internalServerError().body(accessException.getMessage());
        }
    }

    @PostMapping(value = "district")
    public ResponseEntity<String> getAllDistrictByProvinceId(@RequestBody(required = false) FindDistrictByProvinceRequest provinceRequest){
        if(provinceRequest == null){
            return ResponseEntity.badRequest().body("Missing body.");
        }
        try {
            List<DistrictEntity> listDistrict = districtService.getAllDistrictByProvinceId(provinceRequest.getProvinceId());
            return ResponseEntity.ok(gson.toJson(listDistrict));
        }catch (DataAccessException accessException){
            return ResponseEntity.internalServerError().body(accessException.getMessage());
        }
    }
}
