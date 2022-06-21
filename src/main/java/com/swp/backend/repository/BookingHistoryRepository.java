package com.swp.backend.repository;

import com.swp.backend.entity.BookingHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingHistoryRepository extends JpaRepository<BookingHistoryEntity, String> {

}
