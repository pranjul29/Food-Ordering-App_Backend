package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerAddressDao;
import com.upgrad.FoodOrderingApp.service.dao.StateDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// import javax.swing.plaf.nimbus.State;

@Service
public class AddressService {

  @Autowired private AddressDao addressDao;

  @Autowired private StateDao stateDao;

  @Autowired private CustomerAddressDao customerAddressDao;

  public static boolean isValidPinCode(String pinCode) {

    // Regex to check valid pin code.
    String regex = "^[1-9]{1}[0-9]{5}";
    // Compile the ReGex
    Pattern p = Pattern.compile(regex);

    Matcher m = p.matcher(pinCode);
    return m.matches();
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public StateEntity getStateByUUID(String stateUUID)
      throws SaveAddressException, AddressNotFoundException {

    if (stateUUID == null || stateUUID.equals("")) throw new SaveAddressException("SAR-001", "No field can be empty");
    StateEntity stateEntity = stateDao.getStateByUUID(stateUUID);
    if (stateEntity == null) throw new AddressNotFoundException("ANF-002", "No state by this id");

    return stateEntity;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public AddressEntity saveAddress(AddressEntity addressEntity, StateEntity stateEntity)
      throws SaveAddressException {
    if (addressEntity.getCity() == null
        || addressEntity.getFlatBuilNo() == null
        || addressEntity.getLocality() == null
        || addressEntity.getPincode() == null || addressEntity.getCity().equals("")
            || addressEntity.getFlatBuilNo().equals("")
            || addressEntity.getLocality().equals("")
            || addressEntity.getPincode().equals(""))
      throw new SaveAddressException("SAR-001", "No field can be empty");
    else if (!isValidPinCode(addressEntity.getPincode()))
      throw new SaveAddressException("SAR-002", "Invalid pincode");

    addressEntity.setState(stateEntity);
    // CustomerAddressEntity newCustomerAddressEntity =
    // addressDao.saveCustomerAddress(customerAddressEntity);
    // customerAddressEntity.setAddressEntity(newAddressEntity);
    // CustomerAddressEntity newCustomerAddressEntity =
    // addressDao.saveCustomerAddress(customerAddressEntity);
    return addressDao.saveAddress(addressEntity);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public CustomerAddressEntity saveCustomerAddressEntity(
      CustomerEntity customerEntity, AddressEntity addressEntity) {

    CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();
    customerAddressEntity.setCustomerEntity(customerEntity);
    customerAddressEntity.setAddressEntity(addressEntity);

    return customerAddressDao.saveCustomerAddress(customerAddressEntity);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public List<AddressEntity> getAllAddress(CustomerEntity customerEntity) {

    List<AddressEntity> addressEntities = new ArrayList<>();
    List<CustomerAddressEntity> customerAddressEntities =
        customerAddressDao.getAllAddressByCustomer(customerEntity);

    if (customerAddressEntities != null)
      customerAddressEntities.forEach(
          customerAddressEntity -> {
            addressEntities.add(customerAddressEntity.getAddressEntity());
          });

    return addressEntities;
  }

  public AddressEntity getAddressByUUID(String addressUuid, CustomerEntity customerEntity)
      throws AddressNotFoundException, AuthorizationFailedException {

    if (addressUuid == null || addressUuid.equals(""))
      throw new AddressNotFoundException("ANF-005", "Address id can not be empty");

    AddressEntity addressEntity = addressDao.getAddressByUUID(addressUuid);
    if (addressEntity == null)
      throw new AddressNotFoundException("ANF-003", "No address by this id");

    CustomerAddressEntity customerAddressEntity =
        customerAddressDao.getCustomerAddressByAddress(addressEntity);
    if (customerAddressEntity.getCustomerEntity().getUuid() != customerEntity.getUuid())
      throw new AuthorizationFailedException(
          "ATHR-004", "You are not authorized to view/update/delete any one else's address");

    return addressEntity;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public AddressEntity deleteAddress(AddressEntity addressEntity) {
    AddressEntity deletedAddressEntity = addressDao.deleteAddress(addressEntity);
    return deletedAddressEntity;
  }

  public List<StateEntity> getAllStates() {
    List<StateEntity> stateEntities = stateDao.getAllStates();
    return stateEntities;
  }

  public CustomerAddressEntity getCustomerAddress(
      CustomerEntity customerEntity, final AddressEntity addressEntity) {
    return addressDao.getCustomerAddress(customerEntity, addressEntity);
  }
}
