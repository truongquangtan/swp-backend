package com.swp.backend.api.v1.owner.booking_history;

import com.google.gson.Gson;
import com.swp.backend.api.v1.booking_history.BookingHistoryRequest;
import com.swp.backend.api.v1.booking_history.BookingHistoryResponse;
import com.swp.backend.entity.BookingHistoryEntity;
import com.swp.backend.model.BookingHistoryModel;
import com.swp.backend.service.BookingService;
import com.swp.backend.service.SecurityContextService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1/owners")
public class BookingHistoryForOwnerApi {
    private Gson gson;
    private SecurityContextService securityContextService;
    private BookingService bookingService;
    private static final int ITEMS_PER_PAGE_DEFAULT = 5;
    private static final int PAGE_DEFAULT = 1;

    @PostMapping(value = "history-booking")
    public ResponseEntity<String> getBookingHistory(@RequestBody(required = false) BookingHistoryRequest request) {
            return ResponseEntity.internalServerError().body("Error in server: ");
    }
}
