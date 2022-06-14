package com.swp.backend.repository;

import com.swp.backend.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Integer> {
    BookingEntity getBookingEntityBySlotIdAndStatusAndDateIsGreaterThanEqualAndDateIsLessThanEqual(int slotId, String status, Timestamp startTime, Timestamp endTime);
    List<BookingEntity> getBookingEntitiesByAccountIdAndDateIsGreaterThanEqualAndStatusOrderByDateAsc(String userId, Timestamp date, String status);
    List<BookingEntity> getBookingEntitiesByAccountIdOrderByDateDesc(String userId);
}
