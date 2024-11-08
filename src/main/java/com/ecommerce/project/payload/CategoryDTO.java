package com.ecommerce.project.payload;
//payload package will have all the request response objects.
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Defining getters and setters and toString
//This class is like model class but it is not actual model
//We are Decoupling model class into two so that changes made here not effect the data base
//WhenEver we create a category or do anything we can send the object in this form only. We make use of this class only not model class
//This Class CategoryDTO will represent the category at Presentation Layer.
//And the Category model class represent the entity at the data base level.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO
{
    //This is the request Object
    private Long categoryId;
    private String categoryName;

}
