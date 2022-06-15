package com.swp.backend.api.v1.book.booking;

import com.google.gson.Gson;
import com.swp.backend.constance.BookingStatus;
import com.swp.backend.entity.BookingEntity;
import com.swp.backend.model.BookingModel;
import com.swp.backend.service.BookingService;
import com.swp.backend.service.SecurityContextService;
import com.swp.backend.service.YardService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1/yards")
public class BookingApi {
    private SecurityContextService securityContextService;
    private YardService yardService;
    private Gson gson;
    private BookingService bookingService;


    @PostMapping(value = "{yardId}/booking")
    public ResponseEntity<String> bookSlots(@RequestBody(required = false) BookingRequest request, @PathVariable String yardId) {
        BookingResponse response;
        List<BookingEntity> bookingEntities = new ArrayList<>();

        String userId;
        SecurityContext context = SecurityContextHolder.getContext();
        userId = securityContextService.extractUsernameFromContext(context);

        //Request Validation filter
        if (request == null) {
            response = new BookingResponse("Null request body", true, null);
            return ResponseEntity.badRequest().body(gson.toJson(response));
        }
        if (!request.isValid()) {
            response = new BookingResponse("Cannot parse request", true, null);
            return ResponseEntity.badRequest().body(gson.toJson(response));
        }

        //BigYard not available filter
        if (!yardService.isAvailableYard(yardId)) {
            response = new BookingResponse("The Yard entity of this slots is not active or deleted.", true, null);
            return ResponseEntity.badRequest().body(gson.toJson(response));
        }

        //Booking process
        try {
            boolean isError = false, isAllError = true;
            for (BookingModel bookingModel : request.getBookingList()) {
                BookingEntity booking = bookingService.book(userId, bookingModel);
                bookingEntities.add(booking);
                if (booking.getStatus().equals(BookingStatus.FAILED)) {
                    isError = true;
                } else {
                    isAllError = false;
                }
            }
            if (isAllError) {
                response = new BookingResponse("All of your booking slot is error", isError, bookingEntities);
                return ResponseEntity.ok().body(gson.toJson(response));
            }
            response = new BookingResponse(isError ? "There were some booking slot error." : "Booking all slot successfully", isError, bookingEntities);
            return ResponseEntity.ok().body(gson.toJson(response));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Error when save in database: " + ex.getMessage());
        }
    }
}
