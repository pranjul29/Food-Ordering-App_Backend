package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

@Repository
public class StateDao {

    @Autowired
    private EntityManager entityManager;

    public StateEntity getStateByUUID(String stateUUID){
        try {
            return entityManager
                    .createNamedQuery("stateByUUID", StateEntity.class)
                    .setParameter("uuid", stateUUID)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public List<StateEntity> getAllStates() {
        try {
            List<StateEntity> stateEntities = entityManager.createNamedQuery("getAllStates",StateEntity.class).getResultList();
            return stateEntities;
        }catch (NoResultException nre){
            return null;
        }
    }
}
