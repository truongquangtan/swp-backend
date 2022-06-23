package com.swp.backend.api.v1.booking_history.owner;

import com.google.gson.Gson;
import com.swp.backend.api.v1.booking_history.BookingHistoryResponse;
import com.swp.backend.api.v1.booking_history.user.BookingHistoryRequest;
import com.swp.backend.entity.BookingHistoryEntity;
import com.swp.backend.model.BookingHistoryModel;
import com.swp.backend.repository.AccountRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1/owners")
public class BookingHistoryForOwnerApi {
    private Gson gson;
    private SecurityContextService securityContextService;
    private BookingService bookingService;
    private AccountService accountService;
    private AccountRepository accountRepository;
    private BookingHistoryService bookingHistoryService;
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
            String ownerId;
            SecurityContext context = SecurityContextHolder.getContext();
            ownerId = securityContextService.extractUsernameFromContext(context);

            List<BookingHistoryEntity> bookingHistoryEntities = bookingService.getBookingHistoryOfOwner(ownerId, itemsPerPage, page);

            if (bookingHistoryEntities == null) {
                bookingHistoryEntities = new ArrayList<>();
            }

            List<BookingHistoryModel> data = bookingHistoryEntities.stream().map(bookingHistoryEntity -> {
                String createdBy = bookingHistoryEntity.getCreatedBy();
                String displayCreatedBy = createdBy.equals(ownerId) ? "You" : accountRepository.findUserEntityByUserId(bookingHistoryEntity.getCreatedBy()).getFullName();
                return bookingHistoryService.getBookingHistoryModelFromBookingHistoryEntityAndCreatedBy(bookingHistoryEntity, displayCreatedBy);
            }).collect(Collectors.toList());

            int countAllItems = bookingService.countAllHistoryBookingsOfOwner(ownerId);

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
