package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.repositories.CartItemRepository;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.repositories.ProductRepository;
import com.ecommerce.project.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    //AuthUtil is a class that helps us to work with auth related tasks.
    //it has some helper methods like getting the email of logged user
    // and getting username of logged user.
    //This method gives Authenticated user details.
    //If any changes need in the authentication we just change in this class.
    private AuthUtil authUtil;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        //Steps to add a product to cart
        //1.Create a cart if the user doesn't have a cart if user has a cart we need to retrieve the id pf that cart.
        Cart cart = createCart();
        //2.Retrieve the Product details with the productId.
        //3.Perform Validations.
        // Like check if the product is already in the cart
        // and if the product is available then we need to add it into the cart.
        //4.Create cart item.
        // After it pass all the validations we need to add the product to cart.
        //5.Save cart item
        //6.Return the updated cart.
        //once the product is ordered then reduce the product in the stock.


        //Here we are getting product from the product repository
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        //Finding the cart item by cart id and product id
        //if the cart item is exists within the same cart id
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);

        //if cart item already exists in the cart then throw error
        if (cartItem != null) {
            throw new APIException("Product " + product.getProductName() + " already exists in the cart");
        }

        //if the stock is 0 so that the product is not available
        //stock is fetch from the product.
        if (product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available");
        }

        //if we are adding the quantity more than that the stock exist then throw error
        if (product.getQuantity() < quantity) {
            throw new APIException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getQuantity() + ".");
        }

        //finally if all the validations pass create a cart item and save into that.
        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        //saving the cart item into DB
        cartItemRepository.save(newCartItem);

        //Setting the quantity of product by reducing the quantity in the stock
//product.setQuantity(product.getQuantity() - quantity);
        product.setQuantity(product.getQuantity());

        //setting the total price by getting total price from product
        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));

        //save the updated cart in DB
        cartRepository.save(cart);

        //Saved cart is mapping into the cart DTO
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        //here we are getting list of cart items
        List<CartItem> cartItems = cart.getCartItems();

        //created a product stream object
        //the purpose of this below logic is this transforming the product to productDTO
        Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
            //transforming the product to productDTO
            ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
            //setting the quantity
            map.setQuantity(item.getQuantity());
            //returning the map
            return map;
        });

        //Here we are converting into list and setting the products
        cartDTO.setProducts(productStream.toList());
        return cartDTO;

    }


    //Here over ridden the getAllCarts method.
    @Override
    public List<CartDTO> getAllCarts()
    {
        //Getting all the carts with th DB
        List<Cart> carts = cartRepository.findAll();

        if (carts.size() == 0)
        {
            throw new APIException("No Carts Exists");
        }
        //------Here the below is logic to transform the object carts type to cart DTO
        //Here we convert cart and product and put cart in product and return the result.

        //Here converting the cart object type in the list to cartDTO
        List<CartDTO> cartDTOs = carts.stream().map(cart ->
        {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

            //here we get cart items from cart and convert into stream.
            //Here we Convert Product to list of ProductDTO's
            //setting cartDTO as product
            //here we need to handle the quantity

            List<ProductDTO> products = cart.getCartItems().stream().map(cartItem -> {
                //map the item first and next update the quantity
                ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
               //for every productDTO we are setting the quantity.
                //setting the quantity from the cart item
                productDTO.setQuantity(cartItem.getQuantity());
                return productDTO;
            }).collect(Collectors.toList());


            cartDTO.setProducts(products);

            return cartDTO;


            //here we collect the DTO's as list
        }).collect(Collectors.toList());

        return cartDTOs;
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId)
    {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId,cartId);
        //validation
        if(cart == null)
        {
            throw  new ResourceNotFoundException("Cart" , "cartId", cartId);
        }
        //Map cart to the CartDTO
        //it is required because the output is supposed to give response in the form of DTO
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        //we are getting all the items i.e quantity as 10 since we need  1  in the product stock
        //with in a cart i m getting all the products and set quantity as the exact quantity
        //here updating the quantity
        cart.getCartItems().forEach(c ->
                c.getProduct().setQuantity(c.getQuantity()));

        //setting the products in the ProductDTO
        //this map each product in cart to product DTO using model mapper
        List<ProductDTO> products =  cart.getCartItems().stream()
                .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class))
                //collect it as list
                // list of product in the cart
                .toList();
        //setting the products
        cartDTO.setProducts(products);
        return cartDTO;
    }

    // @Transactional this makes sure that method runs within the transactional context. i.e if any method fails then the transaction will roll back.
    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity)
    {
        //finding logged in user mail
        String emailId = authUtil.loggedInEmail();
        //finding cart with the mail
        Cart userCart = cartRepository.findCartByEmail(emailId);
        //getting cart id from the userCart
        Long cartId = userCart.getCartId();

        //getting the cart
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        //getting the product from DB
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));


        //Validation
        //checking the stock
        if(product.getQuantity() == 0)
        {
            throw new APIException(product.getProductName() + " is not available");
        }
        //if the stock is there but the quantity exceeds the stock then raise this
        if (product.getQuantity() < quantity)
        {
            throw new APIException("Please, make an order of the " + product.getProductName()
            + " less than or equal to the quantity " + product.getQuantity() + ".");
        }

        //Here im getting the specified product in the specified cart
        CartItem cartItem =cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);
        //if the product is not there in the cart then throw the error
        if(cartItem == null)
        {
            throw new APIException("Product " + product.getProductName() + " does not exist in the cart!!!");
        }

        //Cart Quantity is going to negative if we delete the item when the item at 0 it goes to -1
        //calculating new quantity
        int newQuantity = cartItem.getQuantity() + quantity;
        //validating to prevent negative quantities
        if (newQuantity < 0)
        {
            throw  new APIException("The Resulting quantity can not be negative");
        }

        //if the new quantity is 0 then the product from the cart

        if (newQuantity == 0)
        {
            deleteProductFromCart(productId, cartId);
        }

        //else update the cart item.
        else
        {
            //Updating the cartItem
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());
            //update total price in the cart
            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
            //save the cart in DB
            cartRepository.save(cart);
        }
        //updating item im getting and stored in updatedCart
        CartItem updatedItem = cartItemRepository.save(cartItem);
        //if the quantity of updated cart item is 0
        if(updatedItem.getQuantity() == 0)
        {
            //deleting the cart item
            cartItemRepository.deleteById(updatedItem.getCartItemId());
        }

        //mapping the cart to CartDTO
        CartDTO  cartDTO = modelMapper.map(cart, CartDTO.class);

        //retrieving items of the cart
        List<CartItem> cartItems = cart.getCartItems();

        //setting quantity in the right way
        Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
            ProductDTO prd = modelMapper.map(item.getProduct(), ProductDTO.class);
            prd.setQuantity(item.getQuantity());
            return prd;
        });

        //setting cartDTO to the productstream to the List
        cartDTO.setProducts(productStream.toList());
        return cartDTO;
    }


    private Cart createCart()
    {
        //here we use authUtil to get loggedEmail of user
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        //if the userCart is not empty return that userCart
        //Here cart exist
        if (userCart != null)
        {
            return userCart;
        }
        //Incase cart not exist then create a cart
        Cart cart = new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtil.loggedInUser());
        //save the newly created cart in the DB
        Cart newCart = cartRepository.save(cart);
        return newCart;
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId)
    {
        //finding cart from i want which product we are passing
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        //finding the cart item
        //from which cart i want which id
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);

        //if cart item null raise error
        if (cartItem == null)
        {
            throw new APIException("Product " + productId);
        }

        //total price of the cart is updated if the cart find
        //1st find total price in cart and -minus  the product price from cart item * quantity
        cart.setTotalPrice(cart.getTotalPrice() -
                (cartItem.getProductPrice() * cartItem.getQuantity()));

        //Getting product associated with cart item
       // Product product = cartItem.getProduct();
        //updating the stock quantity by adding the deleted item to the quantity
       // product.setQuantity(product.getQuantity() + cartItem.getQuantity());

        //delete the item from DB
        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);

        return "Product " + cartItem.getProduct().getProductName() + " removed from cart!!!";
    }

    //Update all the products price in the cart
    @Override
    public void updateProductInCarts(Long cartId, Long productId)
    {
        //fetch the cart by the cart Id
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        //fetch the product from the specified cart
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        //Fetching the cart item
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        //Validation
        if (cartItem == null)
        {
            throw new APIException("Product " + product.getProductName() + " not available in the cart!!!");
        }

        //Here we are updating the product price that reflects the latest cart price
        double cartPrice = cart.getTotalPrice()
                - (cartItem.getProductPrice() * cartItem.getQuantity());

        //setting the product price into the cart item
        cartItem.setProductPrice(product.getSpecialPrice());

        //setting the total price in the cart
        cart.setTotalPrice(cartPrice
                + (cartItem.getProductPrice() * cartItem.getQuantity()));

        //save the cart item in DB
        cartItem = cartItemRepository.save(cartItem);
    }


}






