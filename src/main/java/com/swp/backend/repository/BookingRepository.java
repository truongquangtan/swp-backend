package com.swp.backend.repository;

import com.swp.backend.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Integer> {
    BookingEntity getBookingEntityBySlotIdAndStatusAndDateIsGreaterThanEqualAndDateIsLessThanEqual(int slotId, String status, Timestamp startTime, Timestamp endTime);
}
