package com.swp.backend.api.v1.owner.inactivation.inactivate_sub_yard;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1/owners/me")
public class InactivateSubYardApi {
    private Gson gson;

    @DeleteMapping(value = "yards/{yardId}/sub-yards/{subYardId}")
    public ResponseEntity<String> inactivateSubYard(@PathVariable(name = "yardId") String yardId,
                                                    @PathVariable(name = "subYardId") String subYardId)
    {
        return ResponseEntity.ok().body("");
    }
}
