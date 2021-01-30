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

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity saveCustomer(CustomerEntity customerEntity) throws SignUpRestrictedException {
        String validEmailFormat = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        String validContactNumber = "^(?=.*[0-9])D*\\d{10}\\D*$";
        if(customerEntity.getFirstName() == null || customerEntity.getEmail() == null || customerEntity.getContact_number() == null || customerEntity.getPassword() == null)
            throw new  SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        else if(customerDao.getUserByUsername(customerEntity.getContact_number()) != null)
            throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number.");
        else if(!customerEntity.getEmail().matches(validEmailFormat))
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        else if(!customerEntity.getContact_number().matches(validContactNumber) || customerEntity.getContact_number().length() != 10)
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
        else if(customerEntity.getPassword().length() < 8)
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
        else {
            //check password strength
            boolean hasDigit = false;
            boolean hasUpperCaseLetter = false;
            boolean hasSpecialCharacter = false;
            String sc = "#@$%&*!^";
            for(int i = 0 ; i < customerEntity.getPassword().length() ; i++) {
                if(Character.isDigit(customerEntity.getPassword().charAt(i)))
                    hasDigit = true;
                if(Character.isUpperCase(customerEntity.getPassword().charAt(i)))
                    hasUpperCaseLetter = true;
                if(sc.indexOf(customerEntity.getPassword().charAt(i)) >= 0)
                    hasSpecialCharacter = true;
                if(hasDigit && hasUpperCaseLetter && hasSpecialCharacter)
                    break;
            }
            if(!hasDigit || !hasUpperCaseLetter || !hasSpecialCharacter)
                throw new SignUpRestrictedException("SGR-004", "Weak password!");

            String[] temp = passwordCryptographyProvider.encrypt(customerEntity.getPassword());
            customerEntity.setSalt(temp[0]);
            customerEntity.setPassword(temp[1]);
            return customerDao.createCustomer(customerEntity);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity authenticate(final String username, final String password) throws AuthenticationFailedException {
        CustomerEntity customerEntity = customerDao.getUserByUsername(username);
        if(customerEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
        }
        CustomerAuthEntity preExistingCustomerAuth = customerDao.getCustomerAuthByCustomerId(customerEntity.getId());
        if(preExistingCustomerAuth != null) {
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(24);
            preExistingCustomerAuth.setLoginAt(now);
            preExistingCustomerAuth.setExpiresAt(expiresAt);
            preExistingCustomerAuth.setLogoutAt(null);

            customerDao.updateCustomerAuth(preExistingCustomerAuth);

            return preExistingCustomerAuth;
        }
        String encryptedPassword = PasswordCryptographyProvider.encrypt(password, customerEntity.getSalt());
        if(encryptedPassword.equals(customerEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            CustomerAuthEntity customerAuthEntity = new CustomerAuthEntity();
            customerAuthEntity.setCustomer(customerEntity);
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(24);
            customerAuthEntity.setAccessToken(jwtTokenProvider.generateToken(customerEntity.getUuid(), now, expiresAt));
            customerAuthEntity.setLoginAt(now);
            customerAuthEntity.setExpiresAt(expiresAt);
            customerAuthEntity.setUuid(customerEntity.getUuid());

            customerDao.createAccessToken(customerAuthEntity);

            return customerAuthEntity;
        }
        else
            throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity logout(String accessToken) throws AuthorizationFailedException {
        CustomerAuthEntity customerAuthEntity = customerDao.getCustomerAuthByAccessToken(accessToken);
        if (customerAuthEntity == null)
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        else if(ZonedDateTime.now().compareTo(customerAuthEntity.getExpiresAt()) > 0)
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        else if(customerAuthEntity.getLogoutAt() != null)
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        else {
            customerAuthEntity.setLogoutAt(ZonedDateTime.now());
            return customerAuthEntity;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity updateCustomer(String accessToken, String firstname, String lastname) throws UpdateCustomerException, AuthorizationFailedException {
        CustomerAuthEntity customerAuthEntity = customerDao.getCustomerAuthByAccessToken(accessToken);
        if(customerAuthEntity == null)
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        else if(ZonedDateTime.now().compareTo(customerAuthEntity.getExpiresAt()) > 0)
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        else if(customerAuthEntity.getLogoutAt() != null)
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        if(firstname == null)
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        CustomerEntity customerEntity = customerAuthEntity.getCustomer();
        customerEntity.setFirstName(firstname);
        customerEntity.setLastName(lastname);
        customerDao.updateCustomer(customerEntity);
        return customerAuthEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity updateCustomerPassword(String accessToken, String oldPassword, String newPassword) throws UpdateCustomerException, AuthorizationFailedException {
        CustomerAuthEntity customerAuthEntity = customerDao.getCustomerAuthByAccessToken(accessToken);
        if(oldPassword == null || newPassword == null)
            throw new UpdateCustomerException("UCR-003", "No field should be empty");
        if(customerAuthEntity == null)
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        if(ZonedDateTime.now().compareTo(customerAuthEntity.getExpiresAt()) > 0)
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        if(customerAuthEntity.getLogoutAt() != null)
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        if(newPassword.length() < 8)
            throw new UpdateCustomerException("UCR-001", "Weak password!");
        CustomerEntity customerEntity = customerAuthEntity.getCustomer();
        String encryptedPassword = PasswordCryptographyProvider.encrypt(oldPassword, customerEntity.getSalt());
        if(encryptedPassword.equals(customerEntity.getPassword())) {
            //check password strength
            boolean hasDigit = false;
            boolean hasUpperCaseLetter = false;
            boolean hasSpecialCharacter = false;
            String sc = "#@$%&*!^";
            for(int i = 0 ; i < newPassword.length() ; i++) {
                if(Character.isDigit(newPassword.charAt(i)))
                    hasDigit = true;
                if(Character.isUpperCase(newPassword.charAt(i)))
                    hasUpperCaseLetter = true;
                if(sc.indexOf(newPassword.charAt(i)) >= 0)
                    hasSpecialCharacter = true;
                if(hasDigit && hasUpperCaseLetter && hasSpecialCharacter)
                    break;
            }
            if(!hasDigit || !hasUpperCaseLetter || !hasSpecialCharacter)
                throw new UpdateCustomerException("UCR-001", "Weak password!");

            String[] temp = passwordCryptographyProvider.encrypt(newPassword);
            customerEntity.setSalt(temp[0]);
            customerEntity.setPassword(temp[1]);
            customerDao.updateCustomer(customerEntity);
            return customerAuthEntity;
        }
        else
            throw new UpdateCustomerException("UCR-004", "Incorrect old password!");
    }

    // Create a getCustomer Function
}