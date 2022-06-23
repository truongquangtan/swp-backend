package com.swp.backend.api.v1.booking_history.user;

import com.google.gson.Gson;
import com.swp.backend.api.v1.booking_history.BookingHistoryResponse;
import com.swp.backend.entity.BookingHistoryEntity;
import com.swp.backend.model.BookingHistoryModel;
import com.swp.backend.service.AccountService;
import com.swp.backend.service.BookingHistoryService;
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
@RequestMapping(value = "api/v1/me")
public class BookingHistoryApi {
    private SecurityContextService securityContextService;
    private BookingService bookingService;
    private AccountService accountService;
    private BookingHistoryService bookingHistoryService;
    private Gson gson;
    private static final int ITEMS_PER_PAGE_DEFAULT = 5;
    private static final int PAGE_DEFAULT = 1;

    @PostMapping(value = "history-booking")
    public ResponseEntity<String> getBookingHistory(@RequestBody(required = false) BookingHistoryRequest request) {
        BookingHistoryResponse response;
        int itemsPerPage = ITEMS_PER_PAGE_DEFAULT;
        int page = PAGE_DEFAULT;
        if (request != null) {
            page = request.getPage() > 0 ? request.getPage() : page;
            itemsPerPage = request.getItemsPerPage() > 0 ? request.getItemsPerPage() : itemsPerPage;
        }
        try {
            String userId;
            SecurityContext context = SecurityContextHolder.getContext();
            userId = securityContextService.extractUsernameFromContext(context);

            List<BookingHistoryEntity> bookingHistoryEntities = bookingService.getBookingHistoryOfUser(userId, itemsPerPage, page);
            List<BookingHistoryModel> data = bookingHistoryEntities.stream().map(bookingHistoryEntity -> {
                String createdBy = bookingHistoryEntity.getCreatedBy();
                String displayCreatedBy = createdBy.equals(userId) ? "You" : accountService.getRoleFromUserId(createdBy);
                return bookingHistoryService.getBookingHistoryModelFromBookingHistoryEntityAndCreatedBy(bookingHistoryEntity, displayCreatedBy);
            }).collect(Collectors.toList());

            int countAllItems = bookingService.countAllHistoryBookingsOfUser(userId);

            if (countAllItems <= 0) {
                response = new BookingHistoryResponse("There was no items in data.", 0, 0, data);
                return ResponseEntity.ok().body(gson.toJson(response));
            }

            response = new BookingHistoryResponse("Get history booking successfully", page, countAllItems, data);

            return ResponseEntity.ok().body(gson.toJson(response));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Error in server: " + ex.getMessage());
        }
    }
}
