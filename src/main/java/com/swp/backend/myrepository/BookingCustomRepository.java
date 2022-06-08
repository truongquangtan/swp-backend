package com.swp.backend.myrepository;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class BookingCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;

}
