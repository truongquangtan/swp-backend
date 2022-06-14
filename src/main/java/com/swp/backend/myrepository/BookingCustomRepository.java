package com.swp.backend.myrepository;

import com.swp.backend.constance.BookingStatus;
import com.swp.backend.entity.BookingEntity;
import com.swp.backend.entity.SlotEntity;
import com.swp.backend.utils.DateHelper;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.time.LocalDate;

@Repository
public class BookingCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;
    public static final int ERROR_WHEN_QUERY = -1;
    public int countAllIncomingBookingEntityOfUser(String userId)
    {
        Query query = null;
        try {
            Timestamp now = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);
            String nativeQuery = "SELECT COUNT(*) FROM booking WHERE account_id = ?1 AND date >= ?2 AND status = ?3";
            query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, userId);
            query.setParameter(2, now);
            query.setParameter(3, BookingStatus.SUCCESS);
            return ((BigInteger) query.getSingleResult()).intValue();
        } catch (Exception ex)
        {
            return ERROR_WHEN_QUERY;
        }
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
                                " WHERE b.account_id = ?1 AND b.date >= ?2 AND b.status = ?3 AND s.start_time <= ?4" +
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
