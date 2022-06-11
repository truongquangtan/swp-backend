package com.swp.backend.api.v1.sub_yard.get;

import com.google.gson.Gson;
import com.swp.backend.model.SubYardModel;
import com.swp.backend.model.YardData;
import com.swp.backend.model.YardModel;
import com.swp.backend.service.SubYardService;
import com.swp.backend.service.YardService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1/yards")
public class GetSubYardApi {
    private SubYardService subYardService;
    private YardService yardService;
    private Gson gson;

    @GetMapping(value = "{yardId}")
    public ResponseEntity<String> getSubYardByBigYard(@PathVariable String yardId)
    {
        if(!yardService.isAvailableYard(yardId))
        {
            return ResponseEntity.ok().body(gson.toJson(new SubYardResponse("The yard is not active or deleted.", null)));
        }

        List<SubYardModel> subYards = subYardService.getSubYardsByBigYard(yardId);
        YardModel bigYard = yardService.getYardModelFromYardId(yardId);
        YardData data;

        SubYardResponse response = new SubYardResponse("Get successful", );

        return ResponseEntity.ok().body(gson.toJson(response));
    }
}
