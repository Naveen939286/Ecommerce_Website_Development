package com.ecommerce.project.config;

//This class is going to have all the constants or default values that we wish to have in our project.
public class AppConstants
{
    //In future if we wish to change any sort of defaults we can come at this file and update across the project.
    //put the variables as public because we are make use at Controller.
    //Even if i dont pass the values in postman for getting the categories we get this default values.
     public static final String PAGE_NUMBER = "0";
     public static final String PAGE_SIZE = "50";
     //Adding other variables to allow sorting for the user
     public static final String SORT_CATEGORIES_BY = "categoryId";
     //Pagination For product
     public static final String SORT_PRODUCTS_BY = "productId";
     public static final String SORT_DIR = "asc";



}
