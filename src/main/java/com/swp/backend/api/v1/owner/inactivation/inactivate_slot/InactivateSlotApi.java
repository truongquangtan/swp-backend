package com.swp.backend.api.v1.owner.inactivation.inactivate_slot;

import com.google.gson.Gson;
import com.swp.backend.api.v1.book.cancel_booking.CancelBookingResponse;
import com.swp.backend.exception.CancelBookingProcessException;
import com.swp.backend.service.SecurityContextService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/owner/slots")
public class InactivateSlotApi {
    private Gson gson;
    private SecurityContextService securityContextService;

    @DeleteMapping(value = "{slotId}")
    public ResponseEntity<String> inactivateSlot(@PathVariable int slotId)
    {
        InactivateSlotResponse response;

        String ownerId;
        SecurityContext context = SecurityContextHolder.getContext();
        ownerId = securityContextService.extractUsernameFromContext(context);

        try {
            cancelBookingService.cancelBooking(userId, bookingId, request);
            response = new CancelBookingResponse(true, "Cancel booking successfully");
            return ResponseEntity.ok(gson.toJson(response));
        } catch (CancelBookingProcessException cancelBookingProcessException) {
            response = new CancelBookingResponse(false, cancelBookingProcessException.getFilterMessage());
            return ResponseEntity.ok(gson.toJson(response));
        } catch (Exception exception) {
            response = new CancelBookingResponse(false, "Error when save in database");
            return ResponseEntity.ok(gson.toJson(response));
        }
    }
}
