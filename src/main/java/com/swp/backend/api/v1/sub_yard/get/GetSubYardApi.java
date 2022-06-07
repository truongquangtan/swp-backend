package com.swp.backend.api.v1.sub_yard.get;

import com.google.gson.Gson;
import com.swp.backend.entity.SubYardEntity;
import com.swp.backend.model.SubYardModel;
import com.swp.backend.service.SubYardService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1/sub-yard")
public class GetSubYardApi {
    private SubYardService subYardService;
    private Gson gson;

    @PostMapping(value = "get-by-big-yard")
    public ResponseEntity<String> getSubYardByBigYard(@RequestBody (required = false) GetSubYardRequest getSubYardRequest)
    {
        if(getSubYardRequest == null)
        {
            return ResponseEntity.badRequest().body("Empty body");
        }

        List<SubYardModel> subYards = subYardService.getSubYardsByBigYard(getSubYardRequest.getYardId());
        SubYardResponse response = new SubYardResponse(subYards);

        return ResponseEntity.ok().body(gson.toJson(response));
    }
}