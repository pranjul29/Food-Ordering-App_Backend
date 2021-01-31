package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import javax.swing.plaf.nimbus.State;

@Service
public class AddressService {

    @Autowired
    private AddressDao addressDao;

    public static boolean isValidPinCode(String pinCode)
    {

        // Regex to check valid pin code.
        String regex = "^[1-9]{1}[0-9]{5}";
        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        Matcher m = p.matcher(pinCode);
        return m.matches();
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public StateEntity getStateByUUID(String stateUUID) throws SaveAddressException, AddressNotFoundException {

        if(stateUUID == null)
            throw new SaveAddressException("SAR-001","No field can be empty");
        StateEntity stateEntity = addressDao.getStateByUUID(stateUUID);
        if(stateEntity == null)
            throw new AddressNotFoundException("ANF-002","No state by this id");

        return stateEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity saveAddress(AddressEntity addressEntity, CustomerEntity customerEntity) throws SaveAddressException {
        if(addressEntity.getCity() == null || addressEntity.getFlat_buil_number() == null || addressEntity. getLocality() == null || addressEntity.getPincode() == null || addressEntity.getStateEntity() == null)
            throw new SaveAddressException("SAR-001","No field can be empty");
        else if(isValidPinCode(addressEntity.getPincode()) == false)
            throw new SaveAddressException("SAR-002","Invalid pincode");

        CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();
        customerAddressEntity.setCustomerEntity(customerEntity);

        //CustomerAddressEntity newCustomerAddressEntity = addressDao.saveCustomerAddress(customerAddressEntity);
        AddressEntity newAddressEntity = addressDao.saveAddress(addressEntity);
        customerAddressEntity.setAddressEntity(newAddressEntity);
        CustomerAddressEntity newCustomerAddressEntity = addressDao.saveCustomerAddress(customerAddressEntity);
        return newAddressEntity;
    }
}
