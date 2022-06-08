package com.swp.backend.api.v1.slot;

import com.google.gson.Gson;
import com.swp.backend.api.v1.sub_yard.get.SubYardResponse;
import com.swp.backend.model.Slot;
import com.swp.backend.service.SlotService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1/slot")
public class GetSlotApi {
    private SlotService slotService;
    private Gson gson;

    @PostMapping(value = "get-by-date")
    public ResponseEntity<String> getSlotBySubYardAndDate(@RequestBody(required = false) GetSlotRequest getSlotRequest)
    {
        if(getSlotRequest == null)
        {
            return ResponseEntity.badRequest().body("Empty body");
        }
        if(!getSlotRequest.isValid())
        {
            return ResponseEntity.badRequest().body("Request can not be parsed");
        }

        List<Slot> slots = slotService.getAllSlotInSubYardByDate(getSlotRequest.getSubYardId(), getSlotRequest.getDate());

        if(slots == null)
        {
            return ResponseEntity.internalServerError().body("Error when query!");
        }
        SlotResponse response = new SlotResponse(slots);
        return ResponseEntity.ok().body(gson.toJson(response));
    }
}
