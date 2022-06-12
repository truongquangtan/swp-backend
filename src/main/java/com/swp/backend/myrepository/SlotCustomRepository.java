package com.swp.backend.myrepository;

import com.swp.backend.constance.BookingStatus;
import com.swp.backend.entity.SlotEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.List;

@Repository
public class SlotCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<?> getAllBookedSlotInSubYardByFutureDate(String subYardId, Timestamp date)
    {
        try {
            Query query = null;
            String nativeQuery = "SELECT * FROM slots" +
                    " WHERE (slots.id IN (SELECT slot_id FROM booking WHERE date = ?1 AND status = ?2))" +
                    " AND ref_yard = ?3" +
                    " AND is_active = true";

            query = entityManager.createNativeQuery(nativeQuery, SlotEntity.class);
            query.setParameter(1, date);
            query.setParameter(2, BookingStatus.SUCCESS);
            query.setParameter(3, subYardId);

            if(query != null)
            {
                return query.getResultList();
            }
            return null;
        } catch (NoResultException noResultException)
        {
            return null;
        }
    }

    public List<?> getAllBookedSlotInSubYardByToday(String subYardId, Timestamp today, LocalTime queryTime)
    {
        try
        {
            Query query = null;

            String nativeQuery = "SELECT * FROM slots" +
                    " WHERE (slots.id IN (SELECT slot_id FROM booking WHERE date = ?1 AND status = ?2))" +
                    " AND ref_yard = ?3" +
                    " AND is_active = true" +
                    " AND start_time > ?4";


            query = entityManager.createNativeQuery(nativeQuery, SlotEntity.class);
            query.setParameter(1, today);
            query.setParameter(2, BookingStatus.SUCCESS);
            query.setParameter(3, subYardId);
            query.setParameter(4, queryTime);

            if(query != null)
            {
                return query.getResultList();
            }
            return null;
        } catch (NoResultException noResultException)
        {
            return null;
        }

    }

    public String findYardIdFromSlotId(int slotId)
    {
        try {
            Query query = null;

            String nativeQuery = "SELECT parent_yard" +
                    " FROM sub_yards" +
                    " WHERE id = (SELECT ref_yard FROM slots WHERE id = ?1)";

            query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, slotId);
            if (query != null) {
                return (String) query.getSingleResult();
            }
            return null;
        } catch (NoResultException noResultException)
        {
            return null;
        }
    }

    public String findSubYardIdFromSlotId(int slotId)
    {
        try {
            Query query = null;

            String nativeQuery = "SELECT id" +
                    " FROM sub_yards" +
                    " WHERE id = (SELECT ref_yard FROM slots WHERE id = ?1)";

            query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, slotId);
            if (query != null) {
                return (String) query.getSingleResult();
            }
            return null;
        } catch (NoResultException noResultException)
        {
            return null;
        }
    }
}
