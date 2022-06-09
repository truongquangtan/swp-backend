package com.swp.backend.myrepository;

import com.swp.backend.entity.SubYardEntity;
import com.swp.backend.model.SubYardModel;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class SubYardCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<?> getAllSubYardByBigYard(String bigYardId) {
        Query query = null;

        String nativeQuery = "SELECT s.*" +
                " FROM sub_yards s INNER JOIN yards y ON s.parent_yard = y.id" +
                " WHERE s.parent_yard = ?1" +
                " AND s.is_active = true" +
                " AND y.is_active = true" +
                " AND y.is_deleted = false";

        query = entityManager.createNativeQuery(nativeQuery, SubYardEntity.class);
        query.setParameter(1, bigYardId);

        if (query != null) {
            return query.getResultList();
        } else {
            return null;
        }
    }

    public String getBigYardIdFromSubYard(String subYardId) {
        try {
            Query query = null;

            String nativeQuery = "SELECT s.parent_yard" +
                    " FROM sub_yards s" +
                    " WHERE s.Id = ?1";

            query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, subYardId);

            if (query != null) {
                return (String) query.getSingleResult();
            }
            return null;
        } catch (NoResultException noResultException) {
            return null;
        }
    }
}
