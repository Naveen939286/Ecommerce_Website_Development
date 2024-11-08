package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponce;
import com.ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CategoryController
{
    //For interface we create a instance variable
    //This is a type of interface
    //@AutoWired is a field injection instead of constructor injection we can also use field injection
    @Autowired
   private CategoryService categoryService;


    //API
    //Creating a dummy end point. Not need for our project
//    @GetMapping("/echo")
//    //Accepting message from the url
//   // public ResponseEntity<String> echoMessage(@RequestParam(name = "message", defaultValue = "Hello World")  String message)
//    //It specify null if we don't pass any value to this it is ok if we don't pass message
//    public ResponseEntity<String> echoMessage(@RequestParam(name = "message", required = false)  String message)
//    {
//        return new ResponseEntity<>("Echoed Message: "+message, HttpStatus.OK);
//    }




//   //Constructor Injection
//
//    public CategoryController(CategoryService categoryService) {
//        this.categoryService = categoryService;
//    }

    //Controller intercepting all the API hits to this particular end point
   @GetMapping("/public/categories")
   // @RequestMapping(value = "/public/categories",method = RequestMethod.GET)

    public ResponseEntity<CategoryResponce> getAllCategories(
            //Accepting parameters while getting all the categories Now this paginated request. Here we passing Pagination Details.
            //Here we are making user available to sort.
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder)
    {
        //Here also we pass parameters to the service
        CategoryResponce categoryResponce = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(categoryResponce, HttpStatus.OK);
    }

    //Creating a Category
    //Defining the end point
    @PostMapping("/public/categories")
   // @RequestMapping(value = "/public/categories",method = RequestMethod.POST)
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO)
    {
        //Here What ever service is returning we are saving here as like the method type in service layer.
        CategoryDTO savedCategoryDTO = categoryService.createCategory(categoryDTO);

        //Return the savedcategoryDTO instead of String.
//        return new ResponseEntity<>("Category Added Succesfully", HttpStatus.CREATED);
        //Responce we can get like a DTO object
        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.CREATED);
    }

    //define the end point
    //url consist dynamic value
    @DeleteMapping("/admin/categories/{categoryId}")
   // @RequestMapping(value = "/public/categories",method = RequestMethod.DELETE)
    //path variable map the the variable in path variable to the dynamic value.
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId)
    {
        //After adding status code in service imp class we get error here we put it on try and catch.
        //Here in the controller there is no logic or exceptional handling needed so we forward validations from here.
//        try
//        {
            CategoryDTO deletedCategory= categoryService.deleteCategory(categoryId);
            //return ResponseEntity.ok(status);
            //OR
            return new ResponseEntity<>(deletedCategory, HttpStatus.OK);
            //OR
            //return ResponseEntity.status(HttpStatus.OK).body(status);
       // }
//        catch(ResponseStatusException e)
//        {
//            return new ResponseEntity<>(e.getReason(),e.getStatusCode());
//        }
    }

    //Updating the category use Put Mapping
    //It is just a object so we are not returning any CreteResponce which consist list
    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory( @Valid @RequestBody CategoryDTO categoryDTO,
                                                 @PathVariable Long categoryId)
    {
        //Here in the controller there is no logic or exceptional handling needed so we forward validations from here.
//        try
//        {
            //We also need category id so we took
            //For Updating  over all matter needed so we took category
            CategoryDTO savedCategoryDTO = categoryService.updateCategory(categoryDTO,categoryId);
            //return ResponseEntity.ok(status);
            //OR
            return new ResponseEntity<>(savedCategoryDTO, HttpStatus.OK);
            //OR
            //return ResponseEntity.status(HttpStatus.OK).body(status);
//        }

//        catch(ResponseStatusException e)
//        {
//            return new ResponseEntity<>(e.getReason(),e.getStatusCode());
//        }
    }
}
