package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class CustomerService {

  @Autowired private CustomerDao customerDao;

  @Autowired private PasswordCryptographyProvider passwordCryptographyProvider;

  @Transactional(propagation = Propagation.REQUIRED)
  public CustomerEntity saveCustomer(CustomerEntity customerEntity)
      throws SignUpRestrictedException {
    String validEmailFormat = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
    String validContactNumber = "^(?=.*[0-9])D*\\d{10}\\D*$";
    if (customerEntity.getFirstName() == null
        || customerEntity.getEmail() == null
        || customerEntity.getContact_number() == null
        || customerEntity.getPassword() == null
        || customerEntity.getEmail().equals("")
        || customerEntity.getFirstName().equals("")
        || customerEntity.getContact_number().equals("")
        || customerEntity.getPassword().equals(""))
      throw new SignUpRestrictedException(
          "SGR-005", "Except last name all fields should be filled");
    else if (customerDao.getUserByUsername(customerEntity.getContact_number()) != null)
      throw new SignUpRestrictedException(
          "SGR-001", "This contact number is already registered! Try other contact number.");
    else if (!customerEntity.getEmail().matches(validEmailFormat))
      throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
    else if (!customerEntity.getContact_number().matches(validContactNumber)
        || customerEntity.getContact_number().length() != 10)
      throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
    else if (this.verifyPasswordStrength(customerEntity.getPassword()))
      throw new SignUpRestrictedException("SGR-004", "Weak password!");
    else {
      String[] temp = passwordCryptographyProvider.encrypt(customerEntity.getPassword());
      customerEntity.setSalt(temp[0]);
      customerEntity.setPassword(temp[1]);
      return customerDao.createCustomer(customerEntity);
    }
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public CustomerAuthEntity authenticate(final String username, final String password)
      throws AuthenticationFailedException {
    CustomerEntity customerEntity = customerDao.getUserByUsername(username);
    if (customerEntity == null) {
      throw new AuthenticationFailedException(
          "ATH-001", "This contact number has not been registered!");
    }
    String encryptedPassword =
        PasswordCryptographyProvider.encrypt(password, customerEntity.getSalt());
    CustomerAuthEntity preExistingCustomerAuth =
        customerDao.getCustomerAuthByCustomerId(customerEntity.getId());
    if (preExistingCustomerAuth != null && encryptedPassword.equals(customerEntity.getPassword())) {
      final ZonedDateTime now = ZonedDateTime.now();
      final ZonedDateTime expiresAt = now.plusHours(24);
      preExistingCustomerAuth.setLoginAt(now);
      preExistingCustomerAuth.setExpiresAt(expiresAt);
      preExistingCustomerAuth.setLogoutAt(null);

      customerDao.updateCustomerAuth(preExistingCustomerAuth);

      return preExistingCustomerAuth;
    }
    if (encryptedPassword.equals(customerEntity.getPassword())) {
      JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
      CustomerAuthEntity customerAuthEntity = new CustomerAuthEntity();
      customerAuthEntity.setCustomer(customerEntity);
      final ZonedDateTime now = ZonedDateTime.now();
      final ZonedDateTime expiresAt = now.plusHours(24);
      customerAuthEntity.setAccessToken(
          jwtTokenProvider.generateToken(customerEntity.getUuid(), now, expiresAt));
      customerAuthEntity.setLoginAt(now);
      customerAuthEntity.setExpiresAt(expiresAt);
      customerAuthEntity.setUuid(customerEntity.getUuid());

      customerDao.createAccessToken(customerAuthEntity);

      return customerAuthEntity;
    } else throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public CustomerAuthEntity logout(String accessToken) throws AuthorizationFailedException {
    CustomerAuthEntity customerAuthEntity = customerDao.getCustomerAuthByAccessToken(accessToken);
    if (customerAuthEntity == null)
      throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
    else if (ZonedDateTime.now().compareTo(customerAuthEntity.getExpiresAt()) > 0)
      throw new AuthorizationFailedException(
          "ATHR-003", "Your session is expired. Log in again to access this endpoint.");
    else if (customerAuthEntity.getLogoutAt() != null)
      throw new AuthorizationFailedException(
          "ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
    else {
      customerAuthEntity.setLogoutAt(ZonedDateTime.now());
      return customerAuthEntity;
    }
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public CustomerEntity updateCustomer(CustomerEntity customerEntity)
      throws UpdateCustomerException {
    if (customerEntity.getFirstName() == null || customerEntity.getFirstName().equals(""))
      throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
    customerDao.updateCustomer(customerEntity);
    return customerEntity;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public CustomerEntity updateCustomerPassword(
      String oldPassword, String newPassword, CustomerEntity customerEntity)
      throws UpdateCustomerException {

    if (oldPassword == null
        || newPassword == null
        || oldPassword.equals("")
        || newPassword.equals(""))
      throw new UpdateCustomerException("UCR-003", "No field should be empty");

    // check old password validity
    String encryptedPassword =
        PasswordCryptographyProvider.encrypt(oldPassword, customerEntity.getSalt());
    if (!encryptedPassword.equals(customerEntity.getPassword()))
      throw new UpdateCustomerException("UCR-004", "Incorrect old password!");

    // check new password strength
    if (this.verifyPasswordStrength(newPassword))
      throw new UpdateCustomerException("UCR-001", "Weak password!");

    String[] temp = passwordCryptographyProvider.encrypt(newPassword);
    customerEntity.setSalt(temp[0]);
    customerEntity.setPassword(temp[1]);
    customerDao.updateCustomer(customerEntity);
    return customerEntity;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public CustomerEntity getCustomer(String accessToken) throws AuthorizationFailedException {
    CustomerAuthEntity customerAuthEntity = customerDao.getCustomerAuthByAccessToken(accessToken);
    if (customerAuthEntity == null)
      throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
    else if (ZonedDateTime.now().compareTo(customerAuthEntity.getExpiresAt()) > 0)
      throw new AuthorizationFailedException(
          "ATHR-003", "Your session is expired. Log in again to access this endpoint.");
    else if (customerAuthEntity.getLogoutAt() != null)
      throw new AuthorizationFailedException(
          "ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
    return customerAuthEntity.getCustomer();
  }

  public boolean verifyPasswordStrength(String password) {

    if (password.length() < 8) return true;
    boolean hasDigit = false;
    boolean hasUpperCaseLetter = false;
    boolean hasSpecialCharacter = false;
    String sc = "#@$%&*!^";
    for (int i = 0; i < password.length(); i++) {
      if (Character.isDigit(password.charAt(i))) hasDigit = true;
      if (Character.isUpperCase(password.charAt(i))) hasUpperCaseLetter = true;
      if (sc.indexOf(password.charAt(i)) >= 0) hasSpecialCharacter = true;
      if (hasDigit && hasUpperCaseLetter && hasSpecialCharacter) break;
    }
    return !hasDigit || !hasUpperCaseLetter || !hasSpecialCharacter;
  }
}
