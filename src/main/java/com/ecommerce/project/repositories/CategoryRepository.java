package com.ecommerce.project.repositories;

import com.ecommerce.project.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

//Here we having 2 repository crud and jpa both facilitates to perform CRUD operations easily
// we prefer jpa repository. For both don't need to write the implementation code.
//Spring data JPA will automatically generate the implementation at the runtime to perform all basic CRUD operations.
//In jpa repository we have 2 parameters one is type of entity and another one is type of primary key
public interface CategoryRepository extends JpaRepository<Category,Long>
{

    //We dont need to implement JPA will automatically analyze the declaration here it will understand what you want it automatically generate the implementation
    //We do not need to write any SQL for this Spring Data JPA is do for us.
    //How JPA will exact implementation
    //Here Method name should follow a certain convention for JPA to give the implementation automatically
    //prefix findBy -- indicates this is a query Method
    //CategoryName this is name of the field this will exactly match the field
    //It follow camel case so c Should be Captial
    Category findByCategoryName(String categoryName);
}
