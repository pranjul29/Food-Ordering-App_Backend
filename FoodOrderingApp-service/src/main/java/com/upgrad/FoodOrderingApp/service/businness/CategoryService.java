package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    CategoryDao categoryDao;


//    public CategoryEntity getCategoryByUuid(String uuid) {
//        return  categoryDao.getCategoryByUuid(uuid);
//    }

    public List<CategoryEntity> getAllCategoriesOrderedByName() {
        //Calls getAllCategoriesOrderedByName of categoryDao to get list of CategoryEntity
        return categoryDao.getAllCategoriesOrderedByName();
    }

    public CategoryEntity getCategoryById(String category_id) throws CategoryNotFoundException {
        if(category_id == null || category_id == ""){ //Checking for categoryUuid to be null or empty to throw exception.
            throw new CategoryNotFoundException("CNF-001","Category id field should not be empty");
        }
        CategoryEntity categoryEntity = categoryDao.getCategoryByUuid(category_id);

        if(categoryEntity == null){
            throw new CategoryNotFoundException("CNF-002", "No category by this id");
        }
        return categoryEntity;
    }
}
