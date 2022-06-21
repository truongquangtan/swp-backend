package com.swp.backend.myrepository;

import com.swp.backend.constance.BookingStatus;
import com.swp.backend.model.VoteModel;
import com.swp.backend.utils.DateHelper;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class VoteCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<VoteModel> getAllVoteByUserId(String userId) {
        String nativeQuery = "SELECT votes.id as vote_id, votes.score, votes.comment, yards.name, type_yards.type_name, yards.address, booking.date, slots.start_time, slots.end_time, booking.id as book_id FROM booking" +
                " INNER JOIN slots ON slots.id = booking.slot_id" +
                " INNER JOIN sub_yards ON slots.ref_yard = sub_yards.id" +
                " INNER JOIN yards ON sub_yards.parent_yard = yards.id" +
                " LEFT JOIN votes ON votes.booking_id = booking.id" +
                " INNER JOIN type_yards ON type_yards.id = sub_yards.type_yard" +
                " WHERE (booking.account_id = ?1) AND (booking.status = ?2) AND (booking.date <= ?3)";
        Query query = entityManager.createNativeQuery(nativeQuery);
        query.setParameter(1, userId);
        query.setParameter(2, BookingStatus.SUCCESS);
        query.setParameter(3, DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE));
        List<?> results = query.getResultList();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d/M/y");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm");
        return results.stream().map(result -> {
            Object[] vote = (Object[]) result;
            VoteModel voteModel = VoteModel.builder().build();
            voteModel.setVoteId((String) vote[0]);
            voteModel.setScore((Integer) vote[1]);
            voteModel.setComment((String) vote[2]);
            voteModel.setYardName((String) vote[3]);
            voteModel.setTypeName((String) vote[4]);
            voteModel.setAddress((String) vote[5]);
            voteModel.setDate(((Timestamp) vote[6]).toLocalDateTime().format(dateFormatter));
            voteModel.setStartTime(((Time) vote[7]).toLocalTime().format(timeFormatter));
            voteModel.setEndTime(((Time) vote[8]).toLocalTime().format(timeFormatter));
            voteModel.setBookingId((String) vote[9]);
            return voteModel;
        }).collect(Collectors.toList());
    }
}
