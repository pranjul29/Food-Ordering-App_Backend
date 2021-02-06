package com.upgrad.FoodOrderingApp.service.businness;


import com.upgrad.FoodOrderingApp.service.dao.PaymentDao;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import com.upgrad.FoodOrderingApp.service.exception.PaymentMethodNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {
    @Autowired
    PaymentDao paymentDao;

    public List<PaymentEntity> getAllPaymentMethods() {
        return paymentDao.getAllPaymentMethods();
    }

    public PaymentEntity getPaymentByUUID(String uuid) throws PaymentMethodNotFoundException {
        PaymentEntity paymentEntity = paymentDao.getPaymentByUUID(uuid);
        if(paymentEntity == null){      // Checking if Payment entity is null
            throw new PaymentMethodNotFoundException("PNF-002","No payment method found by this id");
        }
        return paymentEntity;
    }
}
