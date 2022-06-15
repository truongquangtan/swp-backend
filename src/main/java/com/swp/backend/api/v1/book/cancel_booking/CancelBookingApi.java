package com.swp.backend.api.v1.book.cancel_booking;

import com.swp.backend.service.BookingService;
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

    @PutMapping(value = "cancel-booking/{bookingId}")
    public ResponseEntity<String> cancelBooking(@RequestBody(required = false) CancelBookingRequest request, @PathVariable int bookingId)
    {
        CancelBookingResponse response;

        String userId;
        SecurityContext context = SecurityContextHolder.getContext();
        userId = securityContextService.extractUsernameFromContext(context);

        bookingService.cancelBooking(userId, bookingId, request);

        return ResponseEntity.ok().body("");
    }
}
