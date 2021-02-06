package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;

@Service
public class RestaurantService {
    @Autowired
    RestaurantDao restaurantDao;

    public List<RestaurantEntity> getAllRestaurants(){
        return restaurantDao.getAllRestaurants();
    }

    public List<RestaurantEntity> restaurantsByName(String restaurantName) {
        return restaurantDao.getRestaurantsByName(restaurantName);
    }

    public RestaurantEntity restaurantByUUID(String restaurantUuid)throws RestaurantNotFoundException{
        if(restaurantUuid == null||restaurantUuid == ""){ //Checking for restaurantUuid to be null or empty to throw exception.
            throw new RestaurantNotFoundException("RNF-002","Restaurant id field should not be empty");
        }

        //Calls getRestaurantByUuid of restaurantDao to get the  RestaurantEntity
        RestaurantEntity restaurantEntity = restaurantDao.getRestaurantByUUID(restaurantUuid);

        if (restaurantEntity == null){ //Checking for restaurantEntity to be null or empty to throw exception.
            throw new RestaurantNotFoundException("RNF-001","No restaurant by this id");
        }

        return restaurantEntity;


    }

    public List<RestaurantCategoryEntity> restaurantByCategory(Long categoryId){
        return restaurantDao.getRestaurantByCategory(categoryId);
    }

    public RestaurantEntity updateRestaurantRating(final RestaurantEntity restaurantEntity) {
        return restaurantDao.updateRestaurantRating(restaurantEntity);
    }
}
