package com.swp.backend.repository;

import com.swp.backend.entity.YardPictureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YardPictureRepository extends JpaRepository<YardPictureEntity, Integer> {
}
