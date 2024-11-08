package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponce;

import java.util.List;

public interface CategoryService
{
 //Making use of interface to promote loose coupling and modularity in my code.
 //Here we change return type because in Category Responce method the return type is CategoryResponce
//  List<Category> getAllCategories();
 //As we add parameters while getting all categories so we add here also.
  CategoryResponce getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
  CategoryDTO createCategory(CategoryDTO categoryDTO);

  CategoryDTO deleteCategory(Long categoryId);

 CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);
}
