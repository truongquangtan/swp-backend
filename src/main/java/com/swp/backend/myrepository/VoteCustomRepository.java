package com.swp.backend.myrepository;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class VoteCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;

//    public SubYardEntity getAllSubYardRelativeBySubYardId(String subYardId){
//        String nativeQuery = "SELECT * FROM sub_yards " +
//                "WHERE parent_yard " +
//                "IN (SELECT )"
//    }
}
