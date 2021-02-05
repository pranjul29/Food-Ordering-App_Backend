package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

@Repository
public class CustomerAddressDao {

    @Autowired
    private EntityManager entityManager;

    public List<CustomerAddressEntity> getAllAddressByCustomer(final CustomerEntity customerEntity) {
        try {
            return entityManager
                    .createNamedQuery("allAddressByCustomer", CustomerAddressEntity.class)
                    .setParameter("customerEntity", customerEntity)
                    .getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public CustomerAddressEntity getCustomerAddressByAddress(AddressEntity addressEntity) {
        try {
            CustomerAddressEntity customerAddressEntity = entityManager.createNamedQuery("getCustomerAddressByAddress",CustomerAddressEntity.class).setParameter("addressEntity",addressEntity).getSingleResult();
            return customerAddressEntity;
        }catch (NoResultException nre){
            return null;
        }
    }
}
