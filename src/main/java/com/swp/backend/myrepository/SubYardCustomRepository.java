package com.swp.backend.myrepository;

import com.swp.backend.entity.SubYardEntity;
import com.swp.backend.model.SubYardModel;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class SubYardCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<?> getAllSubYardByBigYard(String bigYardId)
    {
        Query query = null;

        String nativeQuery = "SELECT s.*" +
                " FROM sub_yards s INNER JOIN yards y ON s.parent_yard = y.id" +
                " WHERE s.parent_yard = ?1";

        query = entityManager.createNativeQuery(nativeQuery, SubYardEntity.class);
        query.setParameter(1, bigYardId);

        if(query != null)
        {
            return query.getResultList();
        }
        else
        {
            return null;
        }
    }
}
