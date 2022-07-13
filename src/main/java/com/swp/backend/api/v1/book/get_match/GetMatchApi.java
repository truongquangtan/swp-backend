package com.swp.backend.api.v1.book.get_match;

import com.google.gson.Gson;
import com.swp.backend.entity.BookingEntity;
import com.swp.backend.exception.ErrorResponse;
import com.swp.backend.model.MatchModel;
import com.swp.backend.service.BookingService;
import com.swp.backend.service.MatchService;
import com.swp.backend.service.SecurityContextService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1/me/bookings")
public class GetMatchApi {
    private Gson gson;
    private MatchService matchService;
    private BookingService bookingService;
    private SecurityContextService securityContextService;

    @GetMapping(value = "{bookingId}")
    public ResponseEntity<String> getMatch(@PathVariable String bookingId) {
        MatchModel data;

        String userId;
        SecurityContext context = SecurityContextHolder.getContext();
        userId = securityContextService.extractUsernameFromContext(context);

        BookingEntity booking = bookingService.getBookingById(bookingId);

        if (!booking.getAccountId().equals(userId)) {
            ErrorResponse error = ErrorResponse.builder().message("The user is not author of booking").build();
            return ResponseEntity.badRequest().body(gson.toJson(error));
        }

        try {
            data = matchService.getMatchModelFromBookingEntity(booking);
            return ResponseEntity.ok(gson.toJson(data));
        } catch (Exception exception) {
            ErrorResponse error = ErrorResponse.builder().message("Error when query").build();
            return ResponseEntity.ok(gson.toJson(error));
        }
    }
}
