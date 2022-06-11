package com.swp.backend.myrepository;

import com.swp.backend.constance.BookingStatus;
import com.swp.backend.entity.SlotEntity;
import com.swp.backend.utils.DateHelper;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Repository
public class SlotCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<?> getAllBookedSlotInSubYardByDate(String subYardId, LocalDate today, LocalDate queryTime) {
        try {
            Query query = null;
            Timestamp startDate = Timestamp.valueOf(queryTime.toString() + " 00:00:00");
            Timestamp endDate = Timestamp.valueOf(queryTime.toString() + " 23:59:59");
            if (today.compareTo(queryTime) == 0) {
                LocalTime timeNow = LocalTime.now(ZoneId.of(DateHelper.VIETNAM_ZONE));
                String nativeQuery = "SELECT * FROM slots" +
                        " WHERE (slots.id IN (SELECT slot_id FROM booking WHERE date BETWEEN ?1 AND ?2 AND status = ?3))" +
                        " AND ref_yard = ?4" +
                        " AND is_active = true" +
                        " AND start_time > ?5";

                query = entityManager.createNativeQuery(nativeQuery, SlotEntity.class);
                query.setParameter(1, startDate);
                query.setParameter(2, endDate);
                query.setParameter(3, BookingStatus.SUCCESS);
                query.setParameter(4, subYardId);
                query.setParameter(5, timeNow);
            } else {
                String nativeQuery = "SELECT * FROM slots" +
                        " WHERE (slots.id IN (SELECT slot_id FROM booking WHERE date BETWEEN ?1 AND ?2 AND status = ?3))" +
                        " AND ref_yard = ?4" +
                        " AND is_active = true";

                query = entityManager.createNativeQuery(nativeQuery, SlotEntity.class);
                query.setParameter(1, startDate);
                query.setParameter(2, endDate);
                query.setParameter(3, BookingStatus.SUCCESS);
                query.setParameter(4, subYardId);
            }
            return query.getResultList();
        } catch (Exception exception) {
            return null;
        }
    }

    public String findYardIdFromSlotId(int slotId) {
        try {
            Query query = null;

            String nativeQuery = "SELECT parent_yard" +
                    " FROM sub_yards" +
                    " WHERE id = (SELECT ref_yard FROM slots WHERE id = ?1)";

            query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, slotId);
            return (String) query.getSingleResult();
        } catch (NoResultException noResultException) {
            return null;
        }
    }

    public String findSubYardIdFromSlotId(int slotId) {
        try {
            Query query = null;

            String nativeQuery = "SELECT id" +
                    " FROM sub_yards" +
                    " WHERE id = (SELECT ref_yard FROM slots WHERE id = ?1)";

            query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, slotId);
            return (String) query.getSingleResult();
        } catch (NoResultException noResultException) {
            return null;
        }
    }
}
