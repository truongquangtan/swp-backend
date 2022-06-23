package com.swp.backend.service;

import com.swp.backend.entity.BookingEntity;
import com.swp.backend.entity.BookingHistoryEntity;
import com.swp.backend.exception.CancelBookingProcessException;
import com.swp.backend.model.BookingHistoryModel;
import com.swp.backend.model.MatchModel;
import com.swp.backend.model.model_builder.BookingHistoryEntityBuilder;
import com.swp.backend.repository.BookingHistoryRepository;
import com.swp.backend.repository.BookingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BookingHistoryService {
    private MatchService matchService;
    private BookingRepository bookingRepository;
    private BookingHistoryRepository bookingHistoryRepository;

    public BookingHistoryModel getBookingHistoryModelFromBookingHistoryEntityAndCreatedBy(BookingHistoryEntity bookingHistoryEntity, String createdBy) {
        BookingEntity booking = bookingRepository.findBookingEntityById(bookingHistoryEntity.getBookingId());
        MatchModel matchModel = matchService.getMatchModelFromBookingEntity(booking);
        return BookingHistoryModel.buildFromBookingHistoryEntityAndCreatedByAndMatchModel(bookingHistoryEntity, createdBy, matchModel);
    }

    public void saveBookingHistory(BookingEntity bookingEntity, String reason, String createdBy) {
        BookingHistoryEntity bookingHistoryEntity = BookingHistoryEntityBuilder.buildFromBookingEntity(bookingEntity, reason);
        bookingHistoryEntity.setCreatedBy(createdBy);
        bookingHistoryRepository.save(bookingHistoryEntity);
    }
}
