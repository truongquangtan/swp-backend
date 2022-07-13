package com.swp.backend.myrepository;

import com.swp.backend.entity.BookingHistoryEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class BookingHistoryCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<BookingHistoryEntity> getAllBookingHistoryOfOwner(String ownerId, int startIndex, int endIndex) {
        Query query = null;
        try {
            String nativeQuery = "SELECT booking_history.* " +
                    "FROM booking_history INNER JOIN booking ON booking.id = booking_history.booking_id " +
                    "INNER JOIN yards y on booking.big_yard_id = y.id " +
                    "WHERE y.owner_id = ?1 " +
                    "ORDER BY booking_history.created_at DESC";

            query = entityManager.createNativeQuery(nativeQuery, BookingHistoryEntity.class);
            query.setParameter(1, ownerId);
            query.setFirstResult(startIndex);
            query.setMaxResults(endIndex - startIndex + 1);

            List<?> queriedList = query.getResultList();
            if (queriedList == null) {
                return null;
            }
            List<BookingHistoryEntity> result = queriedList.stream().map(objectQueried -> {
                return (BookingHistoryEntity) objectQueried;
            }).collect(Collectors.toList());
            return result;
        } catch (Exception ex) {
            return null;
        }
    }

    public int countAllBookingHistoryOfOwner(String ownerId) {
        Query query = null;
        try {
            String nativeQuery = "SELECT count(*) " +
                    "FROM booking_history INNER JOIN booking ON booking.id = booking_history.booking_id " +
                    "INNER JOIN yards y on booking.big_yard_id = y.id " +
                    "WHERE y.owner_id = ?1";

            query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, ownerId);

            int result = ((BigInteger) query.getSingleResult()).intValue();

            return result;
        } catch (Exception ex) {
            return 0;
        }
    }

    public List<BookingHistoryEntity> getAllBookingHistoryOfUser(String userId, int startIndex, int endIndex) {
        Query query = null;
        try {
            String nativeQuery = "SELECT booking_history.* " +
                    "FROM booking_history INNER JOIN booking ON booking.id = booking_history.booking_id " +
                    "WHERE booking.account_id = ?1 " +
                    "ORDER BY booking_history.created_at DESC";

            query = entityManager.createNativeQuery(nativeQuery, BookingHistoryEntity.class);
            query.setParameter(1, userId);
            query.setFirstResult(startIndex);
            query.setMaxResults(endIndex - startIndex + 1);

            List<?> queriedList = query.getResultList();
            if (queriedList == null) {
                return null;
            }
            return queriedList.stream().map(objectQueried -> {
                return (BookingHistoryEntity) objectQueried;
            }).collect(Collectors.toList());
        } catch (Exception ex) {
            return null;
        }
    }

    public int countAllBookingHistoryOfUser(String userId) {
        Query query = null;
        try {
            String nativeQuery = "SELECT COUNT(*) " +
                    "FROM booking_history INNER JOIN booking ON booking_history.booking_id = booking.id " +
                    "WHERE booking.account_id = ?1";

            query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, userId);

            return ((BigInteger) query.getSingleResult()).intValue();
        } catch (Exception ex) {
            return 0;
        }
    }
}
