package com.swp.backend.repository;

import com.swp.backend.entity.BookingEntity;
import com.swp.backend.entity.BookingHistoryEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface BookingHistoryRepository extends JpaRepository<BookingHistoryEntity, String> {
    List<BookingHistoryEntity> getAllByCreatedByOrderByCreatedAtDesc(String userId, Pageable pageable);
}
