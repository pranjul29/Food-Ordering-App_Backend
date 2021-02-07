package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;

@Entity
@Table(name = "customer_address")
@NamedQueries({
  @NamedQuery(
      name = "allAddressByCustomer",
      query =
          "select cae from CustomerAddressEntity cae where cae.customerEntity = :customerEntity"),
  @NamedQuery(
      name = "getCustomerAddressByAddress",
      query = "select cae from CustomerAddressEntity cae where cae.addressEntity = :addressEntity"),
  @NamedQuery(
      name = "CustomerAddressEntity.getCustomerAddressByAddressEntity",
      query =
          "select ca from CustomerAddressEntity ca where ca.customerEntity=:customer and ca.addressEntity=:address")
})
public class CustomerAddressEntity {

  @Id
  @Column(name = "ID")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "CUSTOMER_ID")
  private CustomerEntity customerEntity;

  @ManyToOne
  @JoinColumn(name = "ADDRESS_ID")
  private AddressEntity addressEntity;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public CustomerEntity getCustomerEntity() {
    return customerEntity;
  }

  public void setCustomerEntity(CustomerEntity customerEntity) {
    this.customerEntity = customerEntity;
  }

  public AddressEntity getAddressEntity() {
    return addressEntity;
  }

  public void setAddressEntity(AddressEntity addressEntity) {
    this.addressEntity = addressEntity;
  }
}
