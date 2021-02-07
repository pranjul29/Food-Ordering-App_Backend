package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Entity
@Table(name = "customer_auth")
@NamedQueries({
  @NamedQuery(
      name = "customerAuthByAccessToken",
      query = "select c from CustomerAuthEntity c where c.accessToken = :accessToken"),
  @NamedQuery(
      name = "customerAuthByCustomerId",
      query = "select c from CustomerAuthEntity c where c.customerEntity.id = :customerId")
})
public class CustomerAuthEntity {

  @Id
  @Column(name = "ID")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "UUID")
  @Size(max = 200)
  private String uuid;

  @ManyToOne
  @JoinColumn(name = "CUSTOMER_ID")
  private CustomerEntity customerEntity;

  @Column(name = "ACCESS_TOKEN")
  @NotNull
  @Size(max = 500)
  private String accessToken;

  @Column(name = "LOGIN_AT")
  @NotNull
  private ZonedDateTime loginAt;

  @Column(name = "EXPIRES_AT")
  @NotNull
  private ZonedDateTime expiresAt;

  @Column(name = "LOGOUT_AT")
  private ZonedDateTime logoutAt;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public CustomerEntity getCustomer() {
    return customerEntity;
  }

  public void setCustomer(CustomerEntity user) {
    this.customerEntity = user;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public ZonedDateTime getLoginAt() {
    return loginAt;
  }

  public void setLoginAt(ZonedDateTime loginAt) {
    this.loginAt = loginAt;
  }

  public ZonedDateTime getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(ZonedDateTime expiresAt) {
    this.expiresAt = expiresAt;
  }

  public ZonedDateTime getLogoutAt() {
    return logoutAt;
  }

  public void setLogoutAt(ZonedDateTime logoutAt) {
    this.logoutAt = logoutAt;
  }
}
