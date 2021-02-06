package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CategoriesListResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryListResponse ;
import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
@CrossOrigin
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @RequestMapping(method = RequestMethod.GET, path = "/category", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoriesListResponse> getAllCategories() {

        List<CategoryEntity> allCategories = categoryService.getAllCategoriesOrderedByName();

        List<CategoryListResponse> categoriesListForResponse = new ArrayList<CategoryListResponse>();

        if(!allCategories.isEmpty()){
        for (CategoryEntity n: allCategories) {
            CategoryListResponse category = new CategoryListResponse();
            category.setCategoryName(n.getCategoryName());
            category.setId(UUID.fromString(n.getUuid()));
            categoriesListForResponse.add(category); // add each category to the List
        }

        CategoriesListResponse categoriesListResponse = new CategoriesListResponse().categories(categoriesListForResponse);
        // return response entity with categoriesListResponse
        return new ResponseEntity<CategoriesListResponse>(categoriesListResponse, HttpStatus.OK);}
        else
            return new ResponseEntity<CategoriesListResponse>(new CategoriesListResponse(),HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.GET, path = "/category/{category_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)

    public ResponseEntity<CategoryDetailsResponse> getCategoryById(@PathVariable(value = "category_id") String category_id) throws CategoryNotFoundException{
        CategoryEntity category = categoryService.getCategoryById(category_id);

        //List<ItemEntity> itemEntities = category.getItems();
        List<ItemList> itemLists =  new ArrayList<>();

        List<ItemEntity> itemEntities = category.getItems();
        for(ItemEntity item :itemEntities){
            ItemList itemList = new ItemList().id(UUID.fromString(item.getUuid())).price(item.getPrice()).itemName(item.getItemName())
                    .itemType(ItemList.ItemTypeEnum.fromValue(item.getType().getValue()));
            itemLists.add(itemList);
        }
        CategoryDetailsResponse categoryDetailsResponse = new CategoryDetailsResponse();
        categoryDetailsResponse.setCategoryName(category.getCategoryName());
        categoryDetailsResponse.setId(UUID.fromString(category.getUuid()));
        categoryDetailsResponse.setItemList(itemLists);
        return new ResponseEntity<CategoryDetailsResponse>(categoryDetailsResponse, HttpStatus.OK);
    }
}
