package com.swp.backend.myrepository;

import com.swp.backend.api.v1.dashboard.owner.YardBookingStatistic;
import com.swp.backend.model.YardStatisticModel;
import com.swp.backend.utils.DateHelper;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DashboardRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<YardStatisticModel> getYardBookingTotalIncomeForOwner(String ownerId, Timestamp startDate, Timestamp endDate)
    {
        Query query = null;
        try
        {
            LocalDate today = LocalDate.now(ZoneId.of(DateHelper.VIETNAM_ZONE));
            Timestamp todayInTimestamp = Timestamp.valueOf(today.toString() + " 00:00:00");
            LocalTime timeNow = LocalTime.now(ZoneId.of(DateHelper.VIETNAM_ZONE));

            String nativeQuery;
            if(endDate.compareTo(todayInTimestamp) < 0)
            {
                nativeQuery = "SELECT b.big_yard_id, y.name, SUM(b.price)" +
                        " FROM booking b INNER JOIN yards y ON b.big_yard_id = y.id" +
                        " WHERE b.status = 'SUCCESS' AND (b.date >= ?1 AND b.date <= ?2)" +
                        " GROUP BY b.big_yard_id, y.name" +
                        " HAVING b.big_yard_id IN (SELECT id FROM yards WHERE owner_id = ?3)";
                query = entityManager.createNativeQuery(nativeQuery);
                query.setParameter(1, startDate);
                query.setParameter(2, endDate);
                query.setParameter(3, ownerId);
            }
            else
            {
                nativeQuery = "SELECT b.big_yard_id, y.name, SUM(b.price)" +
                        " FROM booking b INNER JOIN slots s ON b.slot_id = s.id INNER JOIN yards y ON b.big_yard_id = y.id" +
                        " WHERE b.status = 'SUCCESS' AND (b.date >= ?1 AND (b.date < ?2 OR (b.date = ?2 AND s.end_time < ?3)))" +
                        " GROUP BY b.big_yard_id, y.name" +
                        " HAVING b.big_yard_id IN (SELECT id FROM yards WHERE owner_id = ?4)";
                query = entityManager.createNativeQuery(nativeQuery);
                query.setParameter(1, startDate);
                query.setParameter(2, endDate);
                query.setParameter(3, timeNow);
                query.setParameter(4, ownerId);
            }
            List<?> queriedList = query.getResultList();
            if(queriedList == null)
            {
                return null;
            }

            List<YardStatisticModel> yardStatisticModels = queriedList.stream().map(queriedObject -> {
                Object[] objects = (Object[]) queriedObject;
                return YardStatisticModel.builder().yardId((String) objects[0])
                        .yardName((String) objects[1])
                        .totalIncome(((BigInteger) objects[2]).longValue())
                        .build();
            }).collect(Collectors.toList());
            return yardStatisticModels;
        } catch (Exception ex)
        {
            return null;
        }
    }

    //Not implemented
    public List<YardStatisticModel> getNumberOfYardBookingsForOwner(String ownerId, Timestamp startDate, Timestamp endDate)
    {
        Query query = null;
        try
        {
            LocalDate today = LocalDate.now(ZoneId.of(DateHelper.VIETNAM_ZONE));
            Timestamp todayInTimestamp = Timestamp.valueOf(today.toString() + " 00:00:00");
            LocalTime timeNow = LocalTime.now(ZoneId.of(DateHelper.VIETNAM_ZONE));

            String nativeQuery;
            if(endDate.compareTo(todayInTimestamp) < 0)
            {
                nativeQuery = "SELECT b.big_yard_id, y.name, SUM(b.price)" +
                        " FROM booking b INNER JOIN yards y ON b.big_yard_id = y.id" +
                        " WHERE b.status = 'SUCCESS' AND (b.date >= ?1 AND b.date <= ?2)" +
                        " GROUP BY b.big_yard_id, y.name" +
                        " HAVING b.big_yard_id IN (SELECT id FROM yards WHERE owner_id = ?3)";
                query = entityManager.createNativeQuery(nativeQuery);
                query.setParameter(1, startDate);
                query.setParameter(2, endDate);
                query.setParameter(3, ownerId);
            }
            else
            {
                nativeQuery = "SELECT b.big_yard_id, y.name, SUM(b.price)" +
                        " FROM booking b INNER JOIN slots s ON b.slot_id = s.id INNER JOIN yards y ON b.big_yard_id = y.id" +
                        " WHERE b.status = 'SUCCESS' AND (b.date >= ?1 AND (b.date < ?2 OR (b.date = ?2 AND s.end_time < ?3)))" +
                        " GROUP BY b.big_yard_id, y.name" +
                        " HAVING b.big_yard_id IN (SELECT id FROM yards WHERE owner_id = ?4)";
                query = entityManager.createNativeQuery(nativeQuery);
                query.setParameter(1, startDate);
                query.setParameter(2, endDate);
                query.setParameter(3, timeNow);
                query.setParameter(4, ownerId);
            }
            List<?> queriedList = query.getResultList();
            if(queriedList == null)
            {
                return null;
            }

            List<YardStatisticModel> yardStatisticModels = queriedList.stream().map(queriedObject -> {
                Object[] objects = (Object[]) queriedObject;
                return YardStatisticModel.builder().yardId((String) objects[0])
                        .yardName((String) objects[1])
                        .totalIncome(((BigInteger) objects[2]).longValue())
                        .build();
            }).collect(Collectors.toList());
            return yardStatisticModels;
        } catch (Exception ex)
        {
            return null;
        }
    }

    //Not implemented
    public List<YardStatisticModel> getNumberOfYardBookingCanceledForOwner(String ownerId, Timestamp startDate, Timestamp endDate)
    {
        Query query = null;
        try
        {
            LocalDate today = LocalDate.now(ZoneId.of(DateHelper.VIETNAM_ZONE));
            Timestamp todayInTimestamp = Timestamp.valueOf(today.toString() + " 00:00:00");
            LocalTime timeNow = LocalTime.now(ZoneId.of(DateHelper.VIETNAM_ZONE));

            String nativeQuery;
            if(endDate.compareTo(todayInTimestamp) < 0)
            {
                nativeQuery = "SELECT b.big_yard_id, y.name, SUM(b.price)" +
                        " FROM booking b INNER JOIN yards y ON b.big_yard_id = y.id" +
                        " WHERE b.status = 'SUCCESS' AND (b.date >= ?1 AND b.date <= ?2)" +
                        " GROUP BY b.big_yard_id, y.name" +
                        " HAVING b.big_yard_id IN (SELECT id FROM yards WHERE owner_id = ?3)";
                query = entityManager.createNativeQuery(nativeQuery);
                query.setParameter(1, startDate);
                query.setParameter(2, endDate);
                query.setParameter(3, ownerId);
            }
            else
            {
                nativeQuery = "SELECT b.big_yard_id, y.name, SUM(b.price)" +
                        " FROM booking b INNER JOIN slots s ON b.slot_id = s.id INNER JOIN yards y ON b.big_yard_id = y.id" +
                        " WHERE b.status = 'SUCCESS' AND (b.date >= ?1 AND (b.date < ?2 OR (b.date = ?2 AND s.end_time < ?3)))" +
                        " GROUP BY b.big_yard_id, y.name" +
                        " HAVING b.big_yard_id IN (SELECT id FROM yards WHERE owner_id = ?4)";
                query = entityManager.createNativeQuery(nativeQuery);
                query.setParameter(1, startDate);
                query.setParameter(2, endDate);
                query.setParameter(3, timeNow);
                query.setParameter(4, ownerId);
            }
            List<?> queriedList = query.getResultList();
            if(queriedList == null)
            {
                return null;
            }

            List<YardStatisticModel> yardStatisticModels = queriedList.stream().map(queriedObject -> {
                Object[] objects = (Object[]) queriedObject;
                return YardStatisticModel.builder().yardId((String) objects[0])
                        .yardName((String) objects[1])
                        .totalIncome(((BigInteger) objects[2]).longValue())
                        .build();
            }).collect(Collectors.toList());
            return yardStatisticModels;
        } catch (Exception ex)
        {
            return null;
        }
    }
}
