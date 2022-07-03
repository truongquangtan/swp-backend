package com.swp.backend.myrepository;

import com.swp.backend.model.YardReportModel;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class YardReportCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<YardReportModel> getYardReportModelFromReportId(String reportId)
    {
        Query query = null;

        try
        {
            String nativeQuery = "SELECT yr.id, yr.user_id, yr.yard_id, y.owner_id, a.full_name, y.name, y.address, account_owner.email" +
                    " FROM yard_report yr INNER JOIN yards y ON yr.yard_id = y.id" +
                    "                     INNER JOIN accounts a ON yr.yard_id = a.id" +
                    "                     INNER JOIN accounts account_owner ON y.owner_id = account_owner.id" +
                    " WHERE yr.id = ?1";

            query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, reportId);

            List<?> queriedList = query.getResultList();

            if(queriedList == null)
            {
                return null;
            }

            List<YardReportModel> yardReportModels = queriedList.stream().map(queriedObject -> {
                Object[] objects = (Object[]) queriedObject;
                return YardReportModel.builder().reportId((String) objects[0])
                        .userId((String) objects[1])
                        .yardId((String) objects[2])
                        .ownerId((String) objects[3])
                        .userName((String) objects[4])
                        .yardName((String) objects[5])
                        .yardAddress((String) objects[6])
                        .ownerEmail((String) objects[7])
                        .build();
            }).collect(Collectors.toList());

            return yardReportModels;
        }
        catch (Exception ex)
        {
            return null;
        }
    }
}
