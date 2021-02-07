package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name="restaurant")
@NamedQueries({

        @NamedQuery(name = "restaurantsByRating",query = "SELECT r FROM RestaurantEntity r ORDER BY r.customerRating DESC"),
        @NamedQuery(name = "getRestaurantByUuid",query = "SELECT r FROM RestaurantEntity r WHERE r.uuid = :uuid"),
        @NamedQuery(name = "restaurantsByName",query = "SELECT r FROM  RestaurantEntity r WHERE LOWER(r.restaurantName) LIKE :restaurant_name_low"),
})

public class RestaurantEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private long id;

    @Column(name="AVERAGE_PRICE_FOR_TWO")
    private Integer avgPrice;

    @Column(name = "CUSTOMER_RATING",scale = 2)
    @NotNull
    private double customerRating;

    @Column(name = "NUMBER_OF_CUSTOMERS_RATED")
    @NotNull
    private int numberCustomersRated;

    @Column(name = "PHOTO_URL")
    @NotNull
    @Size(max = 255)
    private String photoUrl;

    @Column(name = "RESTAURANT_NAME")
    @NotNull
    @Size(max = 50)
    private String restaurantName;

    @Column(name = "UUID")
    @NotNull
    @Size(max = 200)
    private String uuid;

    //bi-directional many-to-one association to Order
    @OneToMany(mappedBy="restaurant")
    private List<OrderEntity> orders;

    //bi-directional many-to-one association to Address
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="address_id")
    private AddressEntity address;

    //bi-directional many-to-one association to RestaurantCategory
    @OneToMany(mappedBy="restaurant",fetch = FetchType.EAGER)
    private List<RestaurantCategoryEntity> restaurantCategories;

    //bi-directional many-to-one association to RestaurantItem
    @OneToMany(mappedBy="restaurant")
    private List<RestaurantItemEntity> restaurantItems;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getCustomerRating() {
        return customerRating;
    }

    public void setCustomerRating(double customerRating) {
        this.customerRating = customerRating;
    }

    public Integer getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(Integer avgPrice) {
        this.avgPrice = avgPrice;
    }

    public int getNumberCustomersRated() {
        return numberCustomersRated;
    }

    public void setNumberCustomersRated(int numberCustomersRated) {
        this.numberCustomersRated = numberCustomersRated;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<OrderEntity> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderEntity> orders) {
        this.orders = orders;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    public List<RestaurantCategoryEntity> getRestaurantCategories() {
        return restaurantCategories;
    }

    public void setRestaurantCategories(List<RestaurantCategoryEntity> restaurantCategories) {
        this.restaurantCategories = restaurantCategories;
    }

    public List<RestaurantItemEntity> getRestaurantItems() {
        return restaurantItems;
    }

    public void setRestaurantItems(List<RestaurantItemEntity> restaurantItems) {
        this.restaurantItems = restaurantItems;
    }
}