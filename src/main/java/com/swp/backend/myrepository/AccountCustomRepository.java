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


    public List<?> searchAccount(Integer itemsPerPage, Integer page, Integer role, String keyword, String status, List<String> sortBy, String sort) {
        String queryString = "SELECT * FROM accounts";
        queryString = queryString.concat(buildWhereClauseQuery(role, keyword, status));
        queryString = queryString.concat(buildOrderQuery(sortBy, sort));

        Query query = entityManager.createNativeQuery(queryString, AccountEntity.class);
        return query.getResultList();
    }

    private String buildOrderQuery(List<String> sortBy, String sort) {
        if (sortBy != null && sortBy.size() > 0) {
            String sortValue = (sort != null && sort.equalsIgnoreCase("DESC")) ? "DESC" : "ASC";
            return String.format(" ORDER BY (%s) %s", String.join(", "), sortValue);
        }
        return null;
    }

    private String buildWhereClauseQuery(Integer role, String keyword, String status) {
        String query = null;
        if (role != null || keyword != null || status != null) {
            query = "".concat(" WHERE");
        } else {
            return null;
        }

        if (keyword != null && keyword.trim().length() > 0) {
            query = query.concat(String.format(" (accounts.full_name LIKE '%%%s%%' OR accounts.email LIKE '%%%s%%' OR accounts.phone LIKE '%%%s%%')", keyword, keyword, keyword));
        }

        if (role != null) {
            if (!query.endsWith("WHERE")) {
                query = query.concat(" AND");
            }
            query = query.concat(String.format(" (accounts.role_id = '%d')", role));
        }

        if (status != null && (status.equalsIgnoreCase("true") || status.equalsIgnoreCase("false"))) {
            if (!query.endsWith("WHERE")) {
                query = query.concat(" AND");
            }
            query = query.concat(String.format(" (accounts.active = '%s')", status.toLowerCase()));
        }
        return query;
    }
}
