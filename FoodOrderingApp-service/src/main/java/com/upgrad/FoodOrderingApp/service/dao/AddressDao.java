package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
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

//    public CustomerAddressEntity saveCustomerAddress(CustomerAddressEntity customerAddressEntity){
//        entityManager.persist(customerAddressEntity);
//        return customerAddressEntity;
//    }

    public AddressEntity saveAddress(AddressEntity addressEntity){
        entityManager.persist(addressEntity);
        return addressEntity;
    }

    public AddressEntity getAddressByUUID(String addressUuid) {
        try{
            AddressEntity addressEntity = entityManager.createNamedQuery("getAddressByUuid",AddressEntity.class).setParameter("uuid",addressUuid).getSingleResult();
            return addressEntity;
        }catch (NoResultException nre){
            return null;
        }
    }

    public AddressEntity deleteAddress(AddressEntity addressEntity) {
        entityManager.remove(addressEntity);
        return addressEntity;
    }

    public CustomerAddressEntity getCustomerAddress(CustomerEntity customerEntity, final AddressEntity addressEntity) {
        try {
            return entityManager.createNamedQuery("CustomerAddressEntity.getCustomerAddressByAddressEntity", CustomerAddressEntity.class)
                    .setParameter("customer", customerEntity).setParameter( "address", addressEntity)
                    .getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }
}
