package com.swp.backend.api.v1.slot;

import com.google.gson.Gson;
import com.swp.backend.model.Slot;
import com.swp.backend.service.SlotService;
import com.swp.backend.service.SubYardService;
import com.swp.backend.service.YardService;
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
    private SubYardService subYardService;
    private YardService yardService;
    private Gson gson;

    @PostMapping(value = "get-by-date")
    public ResponseEntity<String> getSlotBySubYardAndDate(@RequestBody(required = false) GetSlotRequest getSlotRequest)
    {
        //Invalid Request Filter
        if(getSlotRequest == null)
        {
            return ResponseEntity.badRequest().body("Empty body");
        }
        if(!getSlotRequest.isValid())
        {
            return ResponseEntity.badRequest().body("Request can not be parsed");
        }

        //BigYard not available filter
        String bigYardId = subYardService.getBigYardIdFromSubYard(getSlotRequest.getSubYardId());
        if(!yardService.isAvailableYard(bigYardId))
        {
            return ResponseEntity.badRequest().body("The Yard entity of this sub yard is not active or deleted.");
        }

        //SubYard not available filter
        if(!subYardService.isActiveSubYard(getSlotRequest.getSubYardId()))
        {
            return ResponseEntity.badRequest().body("SubYard is not active");
        }

        //Successful query
        List<Slot> slots =  slotService.getAllSlotInSubYardByDate(getSlotRequest.getSubYardId(), getSlotRequest.getDate());
        SlotResponse response = new SlotResponse(slots);
        return ResponseEntity.ok().body(gson.toJson(response));
    }

}
