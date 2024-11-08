package com.ecommerce.project.controller;

import com.ecommerce.project.model.Cart;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.service.CartService;
import com.ecommerce.project.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//Entire controller is mapped with /api.
@RequestMapping("/api")
public class CartController
{
    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AuthUtil authUtil;

    //This method is going to return cart DTO
    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId,
                                                    @PathVariable Integer quantity)
    {
        //Since we return cartDTO object
        //calling a method in the service that can add a product
        CartDTO cartDTO = cartService.addProductToCart(productId,quantity);
        return new  ResponseEntity<CartDTO>(cartDTO, HttpStatus.CREATED);
    }

    //This Method is to get all the carts
    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getCarts()
    {
        List<CartDTO> cartDTOS = cartService.getAllCarts();
        return new ResponseEntity<List<CartDTO>>(cartDTOS, HttpStatus.FOUND);
    }

    //This Method will get cart by id
    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getCartById()
    {
        String emailId = authUtil.loggedInEmail();
        //With the email we can find the cart id
        Cart cart = cartRepository.findCartByEmail(emailId);
        Long cartId = cart.getCartId();
        //Here we are getting cart by using mail and cart id
        //why both email and cart id required because the email being passed is users own logged email.
        //for future scalability we pass cart id in future our application need 2 diff carts for desktop and mobile for a single user
       CartDTO cartDTO =  cartService.getCart(emailId,cartId);
        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);

    }

    //This method is to update the product quantity in the cart
    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProduct(@PathVariable Long productId,
                                                     @PathVariable String operation)
    {
        CartDTO cartDTO = cartService.updateProductQuantityInCart(productId,
                //if the operation is delete then -1 else add 1
                operation.equalsIgnoreCase("delete") ? -1 : 1);



        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);
    }

    //delete a product from the controller
    //before deleting some thing we know 1st which cart we are deleting and which product we are deleting
    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId,
                                                        @PathVariable Long productId)
    {
        String status = cartService.deleteProductFromCart(cartId, productId);

        return  new ResponseEntity<String>(status, HttpStatus.OK);
    }
}
