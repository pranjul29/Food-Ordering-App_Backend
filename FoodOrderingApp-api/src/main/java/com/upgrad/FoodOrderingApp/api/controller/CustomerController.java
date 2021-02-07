package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.businness.PasswordCryptographyProvider;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @RequestMapping(method = RequestMethod.POST, path = "/customer/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signup(@RequestBody(required = false) final SignupCustomerRequest signupCustomerRequest) throws SignUpRestrictedException {

        final CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setUuid(UUID.randomUUID().toString());
        customerEntity.setFirstName(signupCustomerRequest.getFirstName());
        customerEntity.setLastName(signupCustomerRequest.getLastName());
        customerEntity.setContact_number(signupCustomerRequest.getContactNumber());
        customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
        customerEntity.setPassword(signupCustomerRequest.getPassword());

        if(signupCustomerRequest.getEmailAddress() =="")
            throw new  SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        final CustomerEntity createdCustomerEntity = customerService.saveCustomer(customerEntity);
        SignupCustomerResponse signupCustomerResponse = new SignupCustomerResponse().id(createdCustomerEntity.getUuid()).status("CUSTOMER SUCCESSFULLY REGISTERED");

        return new ResponseEntity<SignupCustomerResponse>(signupCustomerResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/customer/login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {
        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");
        if(decodedArray.length != 2)
            throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
        CustomerAuthEntity customerAuthEntity = customerService.authenticate(decodedArray[0], decodedArray[1]);
        CustomerEntity customerEntity = customerAuthEntity.getCustomer();
        LoginResponse loginResponse = new LoginResponse().id(customerEntity.getUuid()).message("LOGGED IN SUCCESSFULLY").firstName(customerEntity.getFirstName())
                .lastName(customerEntity.getLastName()).emailAddress(customerEntity.getEmail()).contactNumber(customerEntity.getContact_number());
        System.out.println(customerAuthEntity.getLoginAt());
        System.out.println(customerAuthEntity.getAccessToken());
        HttpHeaders headers = new HttpHeaders();
        headers.add("access-control-expose-headers", "access-token");
        headers.add("access-token", customerAuthEntity.getAccessToken());
        return new ResponseEntity<LoginResponse>(loginResponse, headers, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/customer/logout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> logout(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        String accessToken = authorization.split("Bearer ")[1];

        CustomerAuthEntity customerAuthEntity = customerService.logout(accessToken);
        CustomerEntity customerEntity = customerAuthEntity.getCustomer();

        LogoutResponse logoutResponse = new LogoutResponse().id(customerEntity.getUuid()).message("LOGGED OUT SUCCESSFULLY");

        return new ResponseEntity<LogoutResponse>(logoutResponse,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/customer", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdateCustomerResponse> update(@RequestHeader("authorization") final String authorization,@RequestBody(required = false) UpdateCustomerRequest updateCustomerRequest) throws UpdateCustomerException, AuthorizationFailedException {

        String firstname = updateCustomerRequest.getFirstName();
        if(firstname == null || firstname =="")
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        String accessToken = authorization.split("Bearer ")[1];
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);


        if(firstname != null || firstname=="")
            customerEntity.setFirstName(firstname);

        String lastname = updateCustomerRequest.getLastName();
        if(lastname != null) {
            customerEntity.setLastName(lastname);
        }

        CustomerEntity finalCustomerEntity = customerService.updateCustomer(customerEntity);

        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse().id(finalCustomerEntity.getUuid())
                .firstName(finalCustomerEntity.getFirstName()).lastName(finalCustomerEntity.getLastName()).status("CUSTOMER DETAILS UPDATED SUCCESSFULLY");

        return new ResponseEntity<>(updateCustomerResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/customer/password", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdatePasswordResponse> changePassword(@RequestHeader("authorization") final String authorization, final UpdatePasswordRequest updatePasswordRequest) throws AuthorizationFailedException, UpdateCustomerException {

        String oldPassword = updatePasswordRequest.getOldPassword();
        String newPassword = updatePasswordRequest.getNewPassword();

        if(oldPassword == null || newPassword == null)
            throw new UpdateCustomerException("UCR-003", "No field should be empty");

        String accessToken = authorization.split("Bearer ")[1];
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        //CustomerEntity finalCustomerEntity = customerService.updateCustomerPassword(updatePasswordRequest.getOldPassword(), updatePasswordRequest.getNewPassword(), customerService.getCustomer(accessToken));

        CustomerEntity finalCustomerEntity = customerService.updateCustomerPassword(oldPassword, newPassword, customerEntity);

        //CustomerEntity finalCustomerEntity = customerService.updateCustomer(customerEntity);
        UpdatePasswordResponse updatePasswordResponse = new UpdatePasswordResponse().id(finalCustomerEntity.getUuid()).status("CUSTOMER PASSWORD UPDATED SUCCESSFULLY");
        return new ResponseEntity<UpdatePasswordResponse>(updatePasswordResponse, HttpStatus.OK);
    }
}