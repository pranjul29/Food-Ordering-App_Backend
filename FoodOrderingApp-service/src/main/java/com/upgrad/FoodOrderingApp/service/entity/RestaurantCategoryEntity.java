package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "restaurant_category")
@NamedQueries({

        @NamedQuery(name = "getCategoriesByRestaurant",query = "SELECT r FROM RestaurantCategoryEntity r WHERE r.restaurant= :restaurant ORDER BY r.category.categoryName ASC "),
        @NamedQuery(name = "getRestaurantByCategory",query = "SELECT r FROM RestaurantCategoryEntity r WHERE r.category = :category ORDER BY r.restaurant.customerRating DESC "),
})
public class RestaurantCategoryEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @ManyToOne
    @JoinColumn(name = "RESTAURANT_ID")
    private RestaurantEntity restaurant;

    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID")
    private CategoryEntity category;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public RestaurantEntity getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(RestaurantEntity restaurant) {
        this.restaurant = restaurant;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }
}
