package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

@Repository
public class AddressDao {

    @Autowired
    private EntityManager entityManager;

//    public StateEntity getStateByUUID(String stateUUID){
//        try {
//            return entityManager
//                    .createNamedQuery("stateByUUID", StateEntity.class)
//                    .setParameter("uuid", stateUUID)
//                    .getSingleResult();
//        } catch (NoResultException ex) {
//            return null;
//        }
//    }

    public CustomerAddressEntity saveCustomerAddress(CustomerAddressEntity customerAddressEntity){
        entityManager.persist(customerAddressEntity);
        return customerAddressEntity;
    }

    public AddressEntity saveAddress(AddressEntity addressEntity){
        entityManager.persist(addressEntity);
        return addressEntity;
    }
}
