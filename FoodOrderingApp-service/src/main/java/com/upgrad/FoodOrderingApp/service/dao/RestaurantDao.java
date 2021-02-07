package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantItemEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RestaurantDao {
  @PersistenceContext private EntityManager entityManager;

  // To get the list of RestaurantCategoryEntity from the db by restaurant
  public List<RestaurantCategoryEntity> getCategoriesByRestaurant(
      RestaurantEntity restaurantEntity) {
    try {
      List<RestaurantCategoryEntity> restaurantCategoryEntity =
          entityManager
              .createNamedQuery("getCategoriesByRestaurant", RestaurantCategoryEntity.class)
              .setParameter("restaurant", restaurantEntity)
              .getResultList();
      return restaurantCategoryEntity;
    } catch (NoResultException nre) {
      return null;
    }
  }

  // To get the list of RestaurantCategoryEntity from the db by category
  public List<RestaurantCategoryEntity> getRestaurantByCategory(CategoryEntity categoryEntity) {
    try {
      List<RestaurantCategoryEntity> restaurantCategoryEntities =
          entityManager
              .createNamedQuery("getRestaurantByCategory", RestaurantCategoryEntity.class)
              .setParameter("category", categoryEntity)
              .getResultList();
      return restaurantCategoryEntities;
    } catch (NoResultException nre) {
      return null;
    }
  }

  public List<RestaurantEntity> restaurantsByRating() {
    try {
      List<RestaurantEntity> restaurantEntities =
          entityManager
              .createNamedQuery("restaurantsByRating", RestaurantEntity.class)
              .getResultList();
      return restaurantEntities;
    } catch (NoResultException nre) {
      return null;
    }
  }

  // To get restaurant by UUID from db
  public RestaurantEntity getRestaurantByUuid(String uuid) {
    try {
      RestaurantEntity restaurantEntity =
          entityManager
              .createNamedQuery("getRestaurantByUuid", RestaurantEntity.class)
              .setParameter("uuid", uuid)
              .getSingleResult();
      return restaurantEntity;
    } catch (NoResultException nre) {
      return null;
    }
  }
  // To get the list of restaurant by name from db
  public List<RestaurantEntity> restaurantsByName(String restaurantName) {
    try {
      String restaurantNameLow =
          "%" + restaurantName.toLowerCase() + "%"; // to make a check with lower
      List<RestaurantEntity> restaurantEntities =
          entityManager
              .createNamedQuery("restaurantsByName", RestaurantEntity.class)
              .setParameter("restaurant_name_low", restaurantNameLow)
              .getResultList();
      return restaurantEntities;
    } catch (NoResultException nre) {
      return null;
    }
  }
  // To update the restaurant in the db and return updated restaurant entity.
  public RestaurantEntity updateRestaurantRating(RestaurantEntity restaurantEntity) {
    entityManager.merge(restaurantEntity);
    return restaurantEntity;
  }

  public List<RestaurantItemEntity> getItemsByRestaurant(RestaurantEntity restaurantEntity) {
    try {
      List<RestaurantItemEntity> restaurantItemEntities =
          entityManager
              .createNamedQuery("getItemsByRestaurant", RestaurantItemEntity.class)
              .setParameter("restaurant", restaurantEntity)
              .getResultList();
      return restaurantItemEntities;
    } catch (NoResultException nre) {
      return null;
    }
  }
}
