package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

@Repository
public class CustomerDao {

    @Autowired
    private EntityManager entityManager;

    public CustomerEntity createCustomer(CustomerEntity customerEntity) {
        entityManager.persist(customerEntity);
        return customerEntity;
    }

    public CustomerEntity getUserByUsername(final String username) {
        try {
            return entityManager.createNamedQuery("userByContactNumber", CustomerEntity.class).setParameter("contact_number", username).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public CustomerAuthEntity createAccessToken(final CustomerAuthEntity customerAuthEntity){
        entityManager.persist(customerAuthEntity);
        return customerAuthEntity;
    }

    public CustomerAuthEntity getCustomerAuthByCustomerId(final Integer customerId) {
        try {
            return entityManager.createNamedQuery("customerAuthByCustomerId",
                    CustomerAuthEntity.class).setParameter("customerId", customerId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public CustomerAuthEntity getCustomerAuthByAccessToken(final String accessToken){
        try {
            return entityManager.createNamedQuery("customerAuthByAccessToken",
                    CustomerAuthEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

    public void updateCustomerAuth(CustomerAuthEntity preExistingCustomerAuth) {
        entityManager.merge(preExistingCustomerAuth);
    }

    public void updateCustomer(CustomerEntity customerEntity) {
        entityManager.merge(customerEntity);
    }
}