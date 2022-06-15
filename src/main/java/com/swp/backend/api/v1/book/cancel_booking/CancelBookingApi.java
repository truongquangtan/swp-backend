package com.swp.backend.api.v1.book.cancel_booking;

import com.google.gson.Gson;
import com.swp.backend.exception.CancelBookingProcessException;
import com.swp.backend.service.BookingService;
import com.swp.backend.service.CancelBookingService;
import com.swp.backend.service.SecurityContextService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1/me")
public class CancelBookingApi {
    private SecurityContextService securityContextService;
    private BookingService bookingService;
    private CancelBookingService cancelBookingService;
    private Gson gson;

    @PutMapping(value = "cancel-booking/{bookingId}")
    public ResponseEntity<String> cancelBooking(@RequestBody(required = false) CancelBookingRequest request, @PathVariable int bookingId)
    {
        CancelBookingResponse response;

        String userId;
        SecurityContext context = SecurityContextHolder.getContext();
        userId = securityContextService.extractUsernameFromContext(context);

        if(request == null || !request.isValid())
        {
            response = new CancelBookingResponse(false, "Can not parse request");
            return ResponseEntity.ok(gson.toJson(response));
        }

        try
        {
            cancelBookingService.cancelBooking(userId, bookingId, request);
            response = new CancelBookingResponse(true, "Cancel booking successfully");
            return ResponseEntity.ok(gson.toJson(response));
        }
        catch (CancelBookingProcessException cancelBookingProcessException)
        {
            response = new CancelBookingResponse(false, cancelBookingProcessException.getFilterMessage());
            return ResponseEntity.ok(gson.toJson(response));
        }
        catch (Exception exception)
        {
            throw exception;
            //response = new CancelBookingResponse(false, "Error when save in database");
            //return ResponseEntity.ok(gson.toJson(response));
        }
    }
}
