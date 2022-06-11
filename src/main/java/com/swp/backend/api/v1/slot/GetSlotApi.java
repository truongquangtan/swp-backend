package com.swp.backend.api.v1.slot;

import com.google.gson.Gson;
import com.swp.backend.model.Slot;
import com.swp.backend.service.SlotService;
import com.swp.backend.service.SubYardService;
import com.swp.backend.service.YardService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1")
public class GetSlotApi {
    private SlotService slotService;
    private SubYardService subYardService;
    private YardService yardService;
    private Gson gson;

    @PostMapping(value = "sub-yards/{subYardId}/slots")
    public ResponseEntity<String> getSlotBySubYardAndDate(@PathVariable String subYardId, @RequestBody(required = false) GetSlotRequest getSlotRequest)
    {
        SlotResponse response;

        //Invalid Request Filter
        if(getSlotRequest == null)
        {
            response = new SlotResponse("Empty body", null);
            return ResponseEntity.badRequest().body(gson.toJson(response));
        }
        if(!getSlotRequest.isValid())
        {
            response = new SlotResponse("Request can not be parsed", null);
            return ResponseEntity.badRequest().body(gson.toJson(response));
        }

        //BigYard not available filter
        String bigYardId = subYardService.getBigYardIdFromSubYard(subYardId);
        if(!yardService.isAvailableYard(bigYardId))
        {
            response = new SlotResponse("The Yard entity of this sub yard is not active or deleted.", null);
            return ResponseEntity.badRequest().body(gson.toJson(response));
        }

        //SubYard not available filter
        if(!subYardService.isActiveSubYard(subYardId))
        {
            response = new SlotResponse("SubYard is not active", null);
            return ResponseEntity.badRequest().body(gson.toJson(response));
        }

        //Successful query
        List<Slot> slots =  slotService.getAllSlotInSubYardByDate(subYardId, getSlotRequest.getDate());
        response = new SlotResponse("Get slots successful", slots);
        return ResponseEntity.ok().body(gson.toJson(response));
    }

}
