package com.swp.backend.service;

import com.google.gson.Gson;
import com.swp.backend.constance.BookingStatus;
import com.swp.backend.entity.*;
import com.swp.backend.model.Slot;
import com.swp.backend.model.VoteModel;
import com.swp.backend.myrepository.SubYardCustomRepository;
import com.swp.backend.myrepository.VoteCustomRepository;
import com.swp.backend.repository.BookingRepository;
import com.swp.backend.repository.SlotRepository;
import com.swp.backend.repository.VoteRepository;
import com.swp.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final YardService yardService;
    private final BookingRepository bookingRepository;
    private VoteCustomRepository voteCustomRepository;

    public boolean postVote(String userId, String bookingId, Integer score, String comment) throws DataAccessException {
        try {
            VoteEntity voteEntity = VoteEntity.builder()
                    .id(UUID.randomUUID().toString())
                    .comment(comment)
                    .score(score)
                    .bookingId(bookingId)
                    .date(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE))
                    .build();
            voteRepository.save(voteEntity);
            reUpdateAverageScoreVote(bookingId);
            return true;
        } catch (DataAccessException dataAccessException) {
            dataAccessException.printStackTrace();
            return false;
        }
    }

//    public boolean editVote(String userId, String votedId, int score, String comment) {
//        try {
//            VoteEntity vote = voteRepository.findVoteEntityById(votedId);
//            if (!userId.equals(vote.getUserId())) {
//                return false;
//            }
//            boolean recalculateScore = false;
//            if (vote.getScore() != score) {
//                vote.setScore(score);
//                recalculateScore = true;
//            }
//            vote.setComment(comment);
//            voteRepository.save(vote);
//            if (recalculateScore) {
//                reUpdateAverageScoreVote(vote.getSubYardId());
//            }
//            return true;
//        } catch (Exception exception) {
//            exception.printStackTrace();
//            return false;
//        }
//    }
//
//    public boolean deleteVote(String userId, String votedId) {
//        try {
//            VoteEntity vote = voteRepository.findVoteEntityById(votedId);
//            if (!userId.equals(vote.getUserId())) {
//                return false;
//            }
//            vote.setDeleted(true);
//            voteRepository.save(vote);
//            reUpdateAverageScoreVote(vote.getSubYardId());
//            return true;
//        } catch (Exception exception) {
//            return false;
//        }
//    }
//
    private void reUpdateAverageScoreVote(String bookingId) {
        new Thread(() -> {
            try {
                BookingEntity booking = bookingRepository.getById(bookingId);
                YardEntity yard = yardService.getYardById(booking.getBigYardId());
                List<BookingEntity> bookingListOfBigYard = bookingRepository.findAllByBigYardId(yard.getId());
                List<String> listBookingId = bookingListOfBigYard.parallelStream().map(bookingEntity -> booking.getId()).collect(Collectors.toList());

                List<VoteEntity> votes = voteRepository.findAllByBookingIdInAndDeletedFalse(listBookingId);

                if(votes == null){
                    return;
                }

                float sumScore = votes.stream().reduce(0, (preSum, vote) -> preSum + vote.getScore(), Integer::sum);
                int average = Math.round(sumScore / votes.size());

                yard.setScore(average);
                yard.setNumberOfVote(votes.size());
                yardService.updateYard(yard);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }).start();
    }

    public List<VoteModel> getAllVote(String userId){
        return voteCustomRepository.getAllVoteByUserId(userId);
    }
}
