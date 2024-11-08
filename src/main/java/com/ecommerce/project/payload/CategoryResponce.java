package com.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//Defining getters and setters and toString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponce
{
    //This is the Response object
    //This Response object will have list of objects
    //Here we create response
    private List<CategoryDTO> content;
    //Here we are adding page meta data that we should need to print in the postman. So that front end applications will use the data and display.
    //Adding Pagination details.
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private boolean lastPage;


}
