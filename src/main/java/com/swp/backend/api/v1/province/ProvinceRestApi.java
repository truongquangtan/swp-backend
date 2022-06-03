package com.swp.backend.api.v1.province;

import com.google.gson.Gson;
import com.swp.backend.entity.ProvinceEntity;
import com.swp.backend.service.ProvinceService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1")
@AllArgsConstructor
public class ProvinceRestApi {
    private final ProvinceService provinceService;
    private final Gson gson;

    @GetMapping(value = "province")
    public ResponseEntity<String> getAllProvince(){
        try {
            List<ProvinceEntity> listProvince = provinceService.getAllProvince();
            return ResponseEntity.ok().body(gson.toJson(listProvince));
        }catch (DataAccessException dataAccessException){
            return ResponseEntity.internalServerError().body(dataAccessException.getMessage());
        }
    }
}
