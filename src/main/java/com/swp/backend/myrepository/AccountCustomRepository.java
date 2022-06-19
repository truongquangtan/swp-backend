package com.swp.backend.myrepository;

import com.swp.backend.entity.AccountEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class AccountCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<AccountEntity> getAllUserOrOwnerAccountsByPage(int startIndex, int endIndex)
    {
        Query query = null;
        try
        {
            String nativeQuery = "SELECT * FROM accounts WHERE role_id = 1 OR role_id = 3 ORDER BY create_at DESC";

            query = entityManager.createNativeQuery(nativeQuery, AccountEntity.class);
            query.setFirstResult(startIndex);
            query.setMaxResults(endIndex - startIndex + 1);

            List<?> queriedList = query.getResultList();

            if(queriedList == null)
            {
                return null;
            }

            List<AccountEntity> result = queriedList.stream().map(queriedObject -> {
                return (AccountEntity) queriedObject;
            }).collect(Collectors.toList());

            return result;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public int countAllUserOrOwnerAccounts()
    {
        Query query = null;

        try
        {
            String nativeQuery = "SELECT count(*) FROM accounts WHERE role_id = 1 OR role_id = 3";

            query = entityManager.createNativeQuery(nativeQuery);

            Object result = query.getSingleResult();
            if(result == null)
            {
                return 0;
            }
            return ((BigInteger) result).intValue();
        }
        catch (Exception ex)
        {
            return 0;
        }
    }

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
        return "";
    }

    private String buildWhereClauseQuery(Integer role, String keyword, String status) {
        String query = "";
        if (role != null || keyword != null || status != null) {
            query = "".concat(" WHERE");
        } else {
            return "";
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
