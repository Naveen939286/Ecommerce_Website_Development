package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService
{
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    CartService cartService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;
    @Autowired
    private CartRepository cartRepository;

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {

        //Adding Product to the System Logic we are writing here.
        //If the category we are searching that exists it return the category or else it should throw the exception.
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        //Check if the product is already present or not.
        //Assume product is not found.
        boolean isProductNotPresent = true;
        //Getting products from the category
        List<Product> products = category.getProducts();
        for (Product value : products) {
            if (value.getProductName().equals(productDTO.getProductName())) {
                isProductNotPresent = false;
                break;
            }
        }
        //We can run this if the product is not present.
        if (isProductNotPresent)
        {
            Product product = modelMapper.map(productDTO, Product.class);
            //set the image for that product.
            product.setImage("default.png");
            //set the category for the product
            product.setCategory(category);
            //Calculating the special price
            // eg price - 100 , discount is - 25 , special price == 100(price) - (25*0.01)*100 ===> 75
            double specialPrice = product.getPrice() -
                    (product.getDiscount() * 0.01) * product.getPrice();

            //Setting the specialPrice
            product.setSpecialPrice(specialPrice);
            //After all these save the product into the productRepository i.e DataBase
            Product savedProduct = productRepository.save(product);

            //We can return productDTo with model Mapper
            //Here we convert saved product to ProductDTO with ModelMapper
            return modelMapper.map(savedProduct, ProductDTO.class);

        }
        else
        {
            //If the Product is Presents Already.
            throw new APIException("Product Already Exists");
        }
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder)
    {
        //Make use of these parameters to implement pagination
        //if the text is asc then perform ascending sorting else descending and ignore the case.
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        //With If we are validating and next process as like below
        //Create Instance of Pageable.
        //First get the details from the pageDetails and store in a list of page.
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);

        //1St we are fetching products from Database.
        //Getting All the products from productRepository from DataBase and all the products we store in a list.
        //findall Method will return all the categories that exist in the database.
        Page<Product> pageProducts = productRepository.findAll(pageDetails);
        //From that list of pages we getting data.
        //getContent will return the list of categories
        List<Product> products = pageProducts.getContent();

        //Getting All the products from productRepository from DataBase and all the products we store in a list.
       //List<Product> products = productRepository.findAll();
        //Return type is ProductResponse in this class we having list of ProductDTO so we are need list of ProductDTO.
        //In Presentation layer product is represented in ProductDTO. So we need to convert in ProductDTO.
        //Here we get productDTOs and then we add product reponse object .
        //1---> Convert product into the a stream
        //2----> Using ModelMapper we are mapping every item in the list into productDTO.
        //3-----> Then we are collecting it as a list
        //Entire process is to transfer list of products into productDTOs.
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        //Check Product size is 0 or not
        //Validation
//        if(products.isEmpty())
//        {
//            throw new APIException("No Product Found");
//        }

        //Content is in ProductResponse class
        ProductResponse productResponse = new ProductResponse();
        //Set the  list of productDTOS
        productResponse.setContent(productDTOS);
        //From productResponse we set these items.
        //Updating the Response
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }


    //Getting Products By Category
    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder)
    {
        //Check Product size is 0 or not

        //1--> We have categoryId with this get category.
        //2--> Getting Products by categoryId.

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category", "categoryId" , categoryId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        //method should change here
        //Adding Order by clause to the method that order with price in ascending order.
        //Pass category as parameter
        //Change the type of the method to Page in repository
        Page<Product> pageProducts = productRepository.findByCategoryOrderByPriceAsc(category, pageDetails);
        //Getting list of products
        List<Product> products = pageProducts.getContent();


        //Validation
        if(products.isEmpty())
        {
            throw new APIException(category.getCategoryName() + " category does not have any product");
        }

        //Adding Order by clause to the method that order with price in ascending order.
        //Pass category as parameter
        //List<Product> products = productRepository.findByCategoryOrderByPriceAsc(category);
        //Since we have list of products we will transform them into the list of DTOS. As like above Steps in getAllCategories.

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        //Set the  list of productDTOS
        productResponse.setContent(productDTOS);
        //Updating the Response
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }


    //Keyword --> ex -- if we search rob in search the results related to rob items will return.
    @Override
    public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder)
    {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> pageProducts = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%', pageDetails);
        //Check Product size is 0 or not

        //1--> Find Products with the keyword
        //Here the method involves product Name because keyword is a part of product Name. And use LikeIgnoreCase
        //Like means pattern Matching we are doing pattern matching so we append with percentage.
        //Ignore the case of the keyword.
       // List<Product> products = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%', pageDetails);
        //Transforming list of products to DTOS By Following the same steps.

        List<Product> products = pageProducts.getContent();
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        //Validation
        if(products.isEmpty())
        {
            throw new APIException("Products not Found with Keyword "+ keyword);
        }
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        //Updating the Response
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO)
    {
        //1.Get the exixting product from DB..
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product","ProductId",productId));

        //Converting ProductDTO to product entity.
        Product product = modelMapper.map(productDTO, Product.class);

     //Id is set here.
    //2. Update the product info with the one in request body.
        productFromDb.setProductName(product.getProductName());
        productFromDb.setDescription(product.getDescription());
        productFromDb.setQuantity(product.getQuantity());
        productFromDb.setDiscount(product.getDiscount());
        productFromDb.setPrice(product.getPrice());
        productFromDb.setSpecialPrice(product.getSpecialPrice());
        //3. Save it to the DB.
        Product savedProduct = productRepository.save(productFromDb);

        //After saving we are finding all the carts
        //finding all the carts with product id
        //we need all the carts that contains this product id
        List<Cart> carts = cartRepository.findCartsByProductId(productId);

        //Converting all the carts to cartDTOS
        //each cart to cart DTO
        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

            //Here we are getting cart items within the cart items we are getting product over the list of product DTOS
            //each cart item to product dto
            List<ProductDTO> products = cart.getCartItems().stream()
                    .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());

            //Updating the cart DTO
            cartDTO.setProducts(products);

            return cartDTO;

        }).toList();

        //Here we are update each cart to reflect changes
        //For each cart we are updating the product
        cartDTOs.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));

        //return the updated product
        return modelMapper.map(savedProduct, ProductDTO.class);
    }


    //Deleting a product
    @Override
    public ProductDTO deleteProduct(Long productId)
    {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","productId", productId));

        //Deleting the product from the cart  before delete from DB
        //if we does not need the product in cart if the product is deleted
        List<Cart> carts = cartRepository.findCartsByProductId(productId);

        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), productId));

        productRepository.delete(product);
        //return type is ProductDTO.
        return modelMapper.map(product, ProductDTO.class);
    }

    //Updating product using ProductID
    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        //1. Get the product from DB.
        Product productFromDB = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product","productId",productId));

        //2. Upload the Image to Server.
        //3. Get the file name of uploaded image.
       // String path = "images/";
        String fileName = fileService.uploadImage(path, image);

        //4. Updating the new file name to the product.(Because we fetch the image from DB so we are updating the file name).
        productFromDB.setImage(fileName);

        //5.Save the updated Product
        Product updatedProduct = productRepository.save(productFromDB);

        //6. Return DTO after mapping product to DTO
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

//Upload Image code in Separate Service class




}
