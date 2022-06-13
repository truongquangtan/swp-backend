package com.swp.backend.service;

import com.swp.backend.entity.SubYardEntity;
import com.swp.backend.entity.VoteEntity;
import com.swp.backend.entity.YardEntity;
import com.swp.backend.myrepository.SubYardCustomRepository;
import com.swp.backend.repository.VoteRepository;
import com.swp.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final SubYardService subYardService;
    private final YardService yardService;
    private SubYardCustomRepository subYardCustomRepository;

    public boolean postVote(String userId, String subYarId, int score, String comment) throws DataAccessException {
        try {
            VoteEntity voteEntity = VoteEntity.builder()
                    .id(UUID.randomUUID().toString())
                    .comment(comment)
                    .score(score)
                    .subYardId(subYarId)
                    .date(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE))
                    .userId(userId)
                    .build();
            voteRepository.save(voteEntity);
            reUpdateAverageScoreVote(subYarId);
            return true;
        } catch (DataAccessException dataAccessException) {
            dataAccessException.printStackTrace();
            return false;
        }
    }

    public boolean editVote(String userId, String votedId, int score, String comment) {
        try {
            VoteEntity vote = voteRepository.findVoteEntityById(votedId);
            if(!userId.equals(vote.getUserId())){
                return false;
            }
            boolean recalculateScore = false;
            if (vote.getScore() != score) {
                vote.setScore(score);
                recalculateScore = true;
            }
            vote.setComment(comment);
            voteRepository.save(vote);
            if (recalculateScore) {
                reUpdateAverageScoreVote(vote.getSubYardId());
            }
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean deleteVote(String userId, String votedId){
        try {
            VoteEntity vote = voteRepository.findVoteEntityById(votedId);
            if(!userId.equals(vote.getUserId())){
                return false;
            }
            vote.setDeleted(true);
            voteRepository.save(vote);
            reUpdateAverageScoreVote(vote.getSubYardId());
            return true;
        }catch (Exception exception){
            return false;
        }
    }

    private void reUpdateAverageScoreVote(String subYardId){
        new Thread(() -> {
            try {
                String parentYardId = subYardService.getBigYardIdFromSubYard(subYardId);
                YardEntity bigYard = yardService.getYardById(parentYardId);
                List<SubYardEntity> listRelativeSubYard = subYardCustomRepository.getAllSubYardByBigYard(parentYardId);
                if(listRelativeSubYard == null){
                    return;
                }

                List<String> subYardIds = listRelativeSubYard.stream().map(SubYardEntity::getId).collect(Collectors.toList());
                List<VoteEntity> votes = voteRepository.findBySubYardIdInAndDeletedIsFalse(subYardIds);

                float sumScore = votes.stream().reduce(0, (preSum, vote) -> preSum + vote.getScore(), Integer::sum);
                int average = Math.round(sumScore / votes.size());

                bigYard.setScore(average);
                bigYard.setNumberOfVote(votes.size());
                yardService.updateYard(bigYard);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }).start();
    }
}
