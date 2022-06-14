package com.swp.backend.myrepository;

import com.swp.backend.constance.BookingStatus;
import com.swp.backend.entity.BookingEntity;
import com.swp.backend.utils.DateHelper;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Repository
public class BookingCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;
    public int countAllIncomingBookingEntityOfUser(String userId)
    {
        List<?> queried = getAllOrderedIncomingBookingEntitiesOfUser(userId);
        if(queried == null)
        {
            return 0;
        }
        return queried.size();
    }
    public List<?> getAllOrderedIncomingBookingEntitiesOfUser(String userId)
    {
        Query query = null;
        try {
            LocalDate today = LocalDate.now(ZoneId.of(DateHelper.VIETNAM_ZONE));
            Timestamp startTime = Timestamp.valueOf(today.toString() + " 00:00:00");
            LocalTime timeNow = LocalTime.now(ZoneId.of(DateHelper.VIETNAM_ZONE));
            String nativeQuery = "SELECT b.*" +
                                " FROM (booking b INNER JOIN slots s ON b.slot_id = s.id)" +
                                " WHERE b.account_id = ?1 AND b.date >= ?2 AND b.status = ?3 AND s.start_time >= ?4" +
                                " ORDER BY b.date, s.start_time";
            query = entityManager.createNativeQuery(nativeQuery, BookingEntity.class);
            query.setParameter(1, userId);
            query.setParameter(2, startTime);
            query.setParameter(3, BookingStatus.SUCCESS);
            query.setParameter(4, timeNow);
            return query.getResultList();
        } catch (Exception ex)
        {
            return null;
        }
    }
}
