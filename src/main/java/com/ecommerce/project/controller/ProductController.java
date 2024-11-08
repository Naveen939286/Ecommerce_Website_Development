package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController
{
    @Autowired
    //creating ProductService object
    ProductService productService;


    //Here addmin only allowed to add a product
    @PostMapping("/admin/categories/{categoryId}/product")
    //Here as a responce we add productDTO
    //Adding product with the help of the category Id.
    // Path Variable given in the url and request body bound to that method.
    //Making Decoupling of Presentation Layer from Data Layer. So Use ProductDTO as a Parameter.
    //Instead of Product Entity use ProductDTO.
    //@Valid Validates your Attributes in entity class.
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO,
                                                 @PathVariable Long categoryId)
    {
        //we are return the productDto so we store the result in the productDto object and return that object.
        ProductDTO savedProductDTO = productService.addProduct(categoryId,productDTO);
        return new ResponseEntity<>(savedProductDTO, HttpStatus.CREATED);
    }

    //Getting all products
    @GetMapping("/public/products")
    //Adding Request Parameters for Pagination
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder
    )
    {
        ProductResponse productResponse = productService.getAllProducts(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    //Getting Products by Category
    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable Long categoryId,
                                                                 @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
                                                                 @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                 @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
                                                                 @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder
                                                                 )
    {
        //Here we are getting multiple products. So we can not represent in DTO because for DTO is for single Product .
        //So we use ProductResponse.
       ProductResponse productResponse =  productService.searchByCategory(categoryId, pageNumber,pageSize,sortBy,sortOrder);
       return  new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    //Keyword --> ex -- if we search rob in search the results related to rob items will return.
    public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword,
                                                                @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
                                                                @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
                                                                @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder
                                                                )
    {
      ProductResponse productResponse = productService.searchProductByKeyword(keyword, pageNumber,pageSize,sortBy,sortOrder);
      return new ResponseEntity<>(productResponse, HttpStatus.FOUND);
    }

    //Making Decoupling of Presentation Layer from Data Layer. So Use ProductDTO as a Parameter.
    //Instead of Product Entity use ProductDTO.
    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody ProductDTO productDTO,
                                                        @PathVariable Long productId)
    {
        ProductDTO updatedProductDTO = productService.updateProduct(productId, productDTO);
        return new ResponseEntity<>(updatedProductDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId)
    {
       ProductDTO deletedProduct = productService.deleteProduct(productId);
        return  new ResponseEntity<>(deletedProduct, HttpStatus.OK);
    }

    //Creating an End Point for the image updating
    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId,
                                                         @RequestParam("image") MultipartFile image) throws IOException {
       ProductDTO updatedProduct = productService.updateProductImage(productId, image);
        return  new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }
}

//For Single Product the return type is ProductDTO and for many use ProductResponse.