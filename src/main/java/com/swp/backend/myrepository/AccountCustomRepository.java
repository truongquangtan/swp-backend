package com.swp.backend.myrepository;

import com.swp.backend.entity.AccountEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class AccountCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;


    public List<?> searchAccount(Integer itemsPerPage, Integer page, Integer role, String keyword, String status, List<String> sortBy, String sort){

        String hqlQuery = buildQuery(role, keyword, status,  sortBy, sort);
        Query query = entityManager.createNativeQuery(hqlQuery, AccountEntity.class);

        return query.getResultList();
    }

    private String buildQuery(Integer role, String keyword, String status, List<String> sortBy, String sort){
        String hqlQuery = "SELECT * FROM accounts";
        if(role != null || keyword != null || status != null){
            hqlQuery = hqlQuery.concat(" WHERE");
        }

        if(keyword != null && keyword.trim().length() > 0){
            hqlQuery = hqlQuery.concat(String.format(" (accounts.full_name LIKE '%%%s%%' OR accounts.email LIKE '%%%s%%' OR accounts.phone LIKE '%%%s%%')", keyword, keyword, keyword));
        }

        if(role != null){
            if(!hqlQuery.endsWith("WHERE")){
                hqlQuery = hqlQuery.concat(" AND");
            }
            hqlQuery = hqlQuery.concat(String.format(" (accounts.role_id = '%d')", role));
        }

        if(status != null && (status.equalsIgnoreCase("true") || status.equalsIgnoreCase("false"))){
            if(!hqlQuery.endsWith("WHERE")){
                hqlQuery = hqlQuery.concat(" AND");
            }
            hqlQuery = hqlQuery.concat(String.format(" (accounts.active = '%s')", status.toLowerCase()));
        }
        return hqlQuery;
    }
}
