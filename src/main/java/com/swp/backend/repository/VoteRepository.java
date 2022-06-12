package com.swp.backend.repository;

import com.swp.backend.entity.VoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<VoteEntity, String> {
    VoteEntity findVoteEntityById(String voteId);
}
