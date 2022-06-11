package com.swp.backend.service;

import com.swp.backend.entity.VoteEntity;
import com.swp.backend.entity.YardEntity;
import com.swp.backend.repository.VoteRepository;
import com.swp.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final SubYardService subYardService;
    private final YardService yardService;

    public boolean postVote(String userId, String subYarId, int score, String comment) throws DataAccessException {
        try {
            VoteEntity voteEntity = VoteEntity.builder()
                    .id(UUID.randomUUID().toString())
                    .comment(comment)
                    .score(score)
                    .subYardId(subYarId)
                    .date(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE))
                    .build();
            voteRepository.save(voteEntity);
            new Thread(() -> {
                try {
                    String parentYardId = subYardService.getBigYardIdFromSubYard(subYarId);
                    YardEntity yard = null;
                    if (parentYardId != null) {
                        yard = yardService.getYardById(parentYardId);
                    }
                    if (yard != null) {
                        int currentScores = yard.getScore();
                        int currentNumberOfVote = yard.getNumberOfVote();
                        float newScore = (currentScores * currentNumberOfVote + score) / (float) (currentNumberOfVote + 1);
                        yard.setScore(Math.round(newScore));
                        yard.setNumberOfVote(currentNumberOfVote + 1);
                        yardService.updateYard(yard);
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }).start();
            return true;
        } catch (DataAccessException dataAccessException) {
            dataAccessException.printStackTrace();
            return false;
        }
    }

    public boolean editVote(String votedId, int score, String comment) {
        try {
            VoteEntity vote = voteRepository.findVoteEntityById(votedId);
            boolean recalculateScore = false;
            if (vote == null) {
                return false;
            }
            if (vote.getScore() != score) {
                vote.setScore(score);
                recalculateScore = true;
            }
            vote.setComment(comment);
            voteRepository.save(vote);
            if (recalculateScore) {
                try {
                    new Thread(() -> {
                        String parentYardId = subYardService.getBigYardIdFromSubYard(vote.getSubYardId());


                    }).start();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            return true;

        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }
}
