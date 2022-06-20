package com.swp.backend.repository;

import com.swp.backend.entity.VoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<VoteEntity, String> {
    public VoteEntity findVoteEntityById(String voteId);

    public List<VoteEntity> findByBookingIdInAndDeletedIsFalse(List<String> subYardId);

}
