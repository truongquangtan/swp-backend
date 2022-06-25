package com.swp.backend.repository;

import com.swp.backend.entity.YardPictureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YardPictureRepository extends JpaRepository<YardPictureEntity, Integer> {
    List<YardPictureEntity> getAllByRefId(String refId);
    YardPictureEntity findYardPictureEntityByRefIdAndImage(String refId, String image);
}
