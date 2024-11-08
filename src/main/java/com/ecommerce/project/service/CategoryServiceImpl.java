package com.ecommerce.project.service;
import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponce;
import com.ecommerce.project.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

//Service annotation this tell to spring you need to manage the particular component as a bean and you need to inject
//During runtime constructor in controller will be injected
//the constructor is service interface constructor
@Service
public class CategoryServiceImpl implements CategoryService
{
    //This is the list of categories
  //  private List<Category> categories = new ArrayList();
    //We are generating id in database level so we don't need this nextId.
    //private long nextId=1L;

    @Autowired
    //Getting instance of CategoryRepository Interface no need of list.
    private CategoryRepository categoryRepository;

    //We are getting model Mapper object
    //In future we can add any modules later like product or users or any other module we can store here
    //So we can define that class as Bean so we can Autowired
    @Autowired
    private ModelMapper modelMapper;

    @Override
   // Here we change return type because in Category Responce method the return type is CategoryResponce
    //Here also we accept the parameters.
    public CategoryResponce getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
    //To allow sort we make use of sort.
    //Here if the sort order is equal to asc then sort the elements by ascending order else descending order we uses ternary operator.
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        //We have the facility to pass the Sort object to the parameter of "of" method so that results gives the sorting
        //This is the implementation of Pageable Interface and it represends specific page of data with the pagenation parameters.
        //Here we are usinf of static factory method provide by spring data jpa. And create a new page request instance and assigning to the object of Pageable interface.
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        //findall Method will return all the categories that exist in the database.
        //If No Categories exist then return a message for this we are writing validation.
        //Here we are having list of categories We have to convert list of category to list of DTO's. We have simple way ModelMapper.

        //1St we are fetching categories from Database.
        //With If we are validating and next process as like below

        //First get the details from the pageDetails and store in a list of page.
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        //From that list of pages we getting data.
        //getContent will return the list of categories
        List<Category> categories = categoryPage.getContent();


        //Here 1st we converting categories into stream first
        //Next for every object in the stream we are mapping category to CategoryDTO with the help of modelMapper.
        //for every category we are making use of modelMapper to convert category type to categoryDTO type.
        //And at last convert back to list and storing in the CategoryDToS.
        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();

        //Validation
        if(categories.isEmpty())
            throw new APIException("No Category Created Till Now");

        //Now we have list of categoryDTOS

        CategoryResponce categoryResponce = new CategoryResponce();
        //setting data to categoryResponce
        categoryResponce.setContent(categoryDTOS);
        //Now CategoryResponce is a Wrapper class that contains list of category DTO object.

        //Here we are setting all the meta data in this CategorySeviceImpl class.
        //getNumber method that return the page number
        categoryResponce.setPageNumber(categoryPage.getNumber());
        //This method gives the size of the page
        categoryResponce.setPageSize(categoryPage.getSize());
        categoryResponce.setTotalElements(categoryPage.getTotalElements());
        categoryResponce.setTotalPages(categoryPage.getTotalPages());
        //This method will give boolean response about the page is last page or not.
        categoryResponce.setLastPage(categoryPage.isLast());
        return categoryResponce;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO)
    {
        //We are getting data from CategoryDTO class so 1st we convert and next again convert to the same CategoryDTO
        //Here we are converting categoryDTo to Category.
        Category category = modelMapper.map(categoryDTO, Category.class);
        //When we are creating a category if the category name  already exists then throw API Exception if doesn't exist.
        //Here we are checking category name already exists
        //But the method not exists in the repository
        //What we do is simply create the method  in categoryRepository
        Category categoryFromDB = categoryRepository.findByCategoryName(category.getCategoryName());
        if(categoryFromDB != null)
        {
            throw new APIException("Category with the name " + category.getCategoryName() + " already exists");
        }
        //id is always incremented and not having the duplicate value or null
        //Even if we pass category id will post the data through postman that id is overridden by this id and display this id only.
        //We are generating Id in database level so we don't need the below line
       // category.setCategory_id(nextId++);
        //Here we need to return the saved category so store in a variable
       Category savedCategory = categoryRepository.save(category);
        //Here we are converting to savedCategory type to CategoryDTO and return the CategoryDTO as like the return type.
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId)
    {

//        Category category = categoryRepository.findById(category_id)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Category not found"));

        //Here make use of ResourceNotFoundException Class
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category","CategoryId", categoryId));


        //Apply concepts of streams and functional programming in java
        //converting list into a stream  then i'm apply filter for that stream
        //here we checking every id weather the id matches with our id
        //finding the first match we get it and store in the category.
        //Here we make use of repository first find all the categories and next store in list then same process as above.
//        List<Category> categories=categoryRepository.findAll();
//        Category category = categories.stream()
//                .filter(c ->c.getCategory_id().equals(category_id))
//                .findFirst()
//              //  .orElse(null);
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource Not Found"));
        //inaddition to message we through the status code by using ResponseStatusException and status code can write using HTTP status enum
        //the above orElse gives if the category is found deleted or else if not found return null
        //remove the matched category from our categories list
     //   if(category==null)
//        {
//            return "Category Not Found!";
//        }
//        categories.remove(category);


        //instead of remove use delete.
        categoryRepository.delete(category);
        //We are returing the deleted category
        return modelMapper.map(category, CategoryDTO.class) ;
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        //Instead of fetching all the categories just fetch the category that i need to update optimizing the code.
        //   List<Category> categories = categoryRepository.findAll();
        //return type is optional so made this change
//      Optional <Category> savedCategoryOptional = categoryRepository.findById(category_id);

        //If we doesn't get any value from savedCategoryOptional then we throw exception and printing message.
        //If value not exist in the database then return Error message with status code
//        Category savedCategory = savedCategoryOptional
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Resourse Not Found"));


        //Getting id which we need to update if no id exist in the data base we print the error message with the status code.
//        Category savedCategory = categoryRepository.findById(category_id)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Resourse Not Found"));

        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        //We are having category object which is converted from categoryDTO to category
        Category category = modelMapper.map(categoryDTO, Category.class);
        //ID has been set here
        category.setCategoryId(categoryId);

        //Saving the category into the database
        savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);

//        Optional<Category> optionalcategory =categories.stream()
//                .filter(c -> c.getCategory_id().equals(category_id))
//                .findFirst();
//                //findFirst will return optional so we rename to optional above while storing.
//        //We can also write .get but if category not found we get error so we use if statement. instead of throughing exception
//        if(optionalcategory.isPresent())
//        {
//            Category existingCategory = optionalcategory.get();
//            //Update the category with user has given updated details
//            existingCategory.setCategory_name(category.getCategory_name());
//            //1st save the existing category and then return
//            Category savedCategory = categoryRepository.save(existingCategory);
//            return savedCategory;
//        }
//        else
//        {
//            //if the category not found then throw the below message.
//           throw  new ResponseStatusException(HttpStatus.NOT_FOUND,"Category Not Found");
//        }

    }
}
