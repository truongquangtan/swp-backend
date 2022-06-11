package com.swp.backend.api.v1.booking;

import com.google.gson.Gson;
import com.swp.backend.api.v1.slot.SlotResponse;
import com.swp.backend.model.BookingModel;
import com.swp.backend.service.SecurityContextService;
import com.swp.backend.service.SlotService;
import com.swp.backend.service.SubYardService;
import com.swp.backend.service.YardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Book;

@RestController
@RequestMapping(value = "api/v1/booking")
public class BookingApi {
    private SecurityContextService securityContextService;
    private YardService yardService;
    private SlotService slotService;
    private SubYardService subYardService;
    private Gson gson;


    @PostMapping(value = "book")
    public ResponseEntity<String> bookSlots(@RequestBody(required = false) BookingRequest request)
    {
        BookingResponse response;

        SecurityContext context = SecurityContextHolder.getContext();
        String userIdRequest = securityContextService.extractUsernameFromContext(context);

        //Request Validation filter
        if(request == null)
        {
            response = new BookingResponse("Null request body", null);
            return ResponseEntity.badRequest().body(gson.toJson(response));
        }
        if(!request.isValid())
        {
            response = new BookingResponse("Cannot parse request", null);
            return ResponseEntity.badRequest().body(gson.toJson(response));
        }

        //BigYard not available filter
        for(BookingModel bookingModel : request.getBookingModels())
        {
            String bigYardId = slotService.getYardIdFromSlotId(bookingModel.getSlotId());
            if(bigYardId == null)
            {
                response = new BookingResponse("Cannot find big yard from slot id", null);
                return ResponseEntity.badRequest().body(gson.toJson(response));
            }
            if(!yardService.isAvailableYard(bigYardId))
            {
                response = new BookingResponse("The Yard entity of this slot is not active or deleted.", null);
                return ResponseEntity.badRequest().body(gson.toJson(response));
            }
        }

        //SubYard not available filter
        for(BookingModel bookingModel : request.getBookingModels())
        {
            String subYardId = slotService.getSubYardIdFromSlotId(bookingModel.getSlotId());
            if(subYardId == null)
            {
                response = new BookingResponse("Cannot find sub yard from slot id " + bookingModel.getSlotId(), null);
                return ResponseEntity.badRequest().body(gson.toJson(response));
            }
            if(!subYardService.isActiveSubYard(subYardId))
            {
                response = new BookingResponse("SubYard of slot id " + bookingModel.getSlotId() + " is not active", null);
                return ResponseEntity.badRequest().body(gson.toJson(response));
            }
        }

        //Slot not available filter

        return ResponseEntity.ok().body("");
    }
}
