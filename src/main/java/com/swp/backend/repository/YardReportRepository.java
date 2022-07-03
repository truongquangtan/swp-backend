package com.swp.backend.repository;

import com.swp.backend.entity.YardReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YardReportRepository extends JpaRepository<YardReportEntity, String> {

}
