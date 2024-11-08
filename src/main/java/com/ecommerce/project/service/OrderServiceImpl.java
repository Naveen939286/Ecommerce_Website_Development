package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.*;
import com.ecommerce.project.payload.OrderDTO;
import com.ecommerce.project.payload.OrderItemDTO;
import com.ecommerce.project.repositories.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService
{
   @Autowired
   private CartRepository cartRepository;

   @Autowired
   private AddressRepository addressRepository;

   @Autowired
   private OrderRepository orderRepository;

   @Autowired
   private OrderItemRepository orderItemRepository;

   @Autowired
   private PaymentRepository paymentRepository;

   @Autowired
   private CartService cartService;

   @Autowired
   private ModelMapper modelMapper;

    @Autowired
    private ProductRepository productRepository;

    @Override
    //if anything falls in side this method we don't want anything to commit
    @Transactional
    public OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod,
                               String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage)
    {
       //This method tells how a order is going to place
        //---------Steps-----------
        //1. Validation weather the cart is empty or not
        //2. Then create a order (This involves creating order items , creating order objects and save into the Data base,save the payment information too)
        //3. once the order successful that order items and payments saved into Data base.
        // Then Clear the Cart.
        //4. And we want to update the stock. Remove the ordered items from the stock

        //Getting the cart by email id
        Cart cart = cartRepository.findCartByEmail(emailId);
        //Validation
        if(cart == null)
        {
            throw new ResourceNotFoundException("Cart", "email", emailId);
        }


        //Here we get address by Id
        //if the address not there we throw exception i.e in order address not there we throw exception
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        //once the two validations pass create the order object
        Order order = new Order();
        //Saving the information related to order
        order.setEmail(emailId);
        //order date is now
        order.setOrderDate(LocalDate.now());
        //saving the total price that we get from the cart that include the discount all calculation done there
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted !");
        //Setting the address against the order
        order.setAddress(address);

        //Creating the payment information.
        //1.Create the payment object
        //2. Set the order within payment(Helps us to identify this particular payment belongs to which order)
        //3. save the payment info in Data base
        //4. payment object is set for the order object then only the payment updated.(In this step only the order is saved)
        Payment payment = new Payment(paymentMethod, pgPaymentId,pgStatus,pgResponseMessage,pgName);
        payment.setOrder(order);
        //save the payment info in Data base
        payment = paymentRepository.save(payment);
        //Payment info is supposed to be set against the order.
        order.setPayment(payment);

        //Saving the order in DB
        Order savedOrder = orderRepository.save(order);

        //Getting the list of cart items
        //if empty throw error
        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty())
        {
            throw new APIException("Cart is Empty");
        }

        //if the cart is not empty then we create a list of order items
        List<OrderItem> orderItems = new ArrayList<>();
        //With the help of for we loop over here
        //here we are creating the instance of order item
        // We are getting all the information from the cart item and setting against the order item.
        for (CartItem cartItem : cartItems)
        {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setOrder(savedOrder);
            //At last order item set into the order items
            orderItems.add(orderItem);
        }
        //Saving all the order items to Data Base
        orderItems = orderItemRepository.saveAll(orderItems);

        //for each item in the cart need to be updated
        //updating the stock
        cart.getCartItems().forEach(item -> {

            //for every cart this gets the quantity and
            // reduce the stock in Database against the product.


            //getting the items quantity in the cart
            int quantity = item.getQuantity();

            //getting the product from the cart and store in product object
            Product product = item.getProduct();

            //Reducing the stock
            product.setQuantity(product.getQuantity() - quantity);

            //save the updated product in Data Base
            productRepository.save(product);

            //Removing items from the cart i.e empty the cart
            //make use of this method deleteProductFromCart in cartService we are deleting items from cart.
            //this deleteProductFromCart method accepts the cartId and productId.
            cartService.deleteProductFromCart(cart.getCartId(),item.getProduct().getProductId());

        });

        //Getting the object of orderDTO
        //Converting savedOrder into the orderDTO
        //single one order so we don't use any loop
        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);

        // for every order item in orderItems we are converting into the OrderItemDTO type
        orderItems.forEach(item -> orderDTO.getOrderItems().add(modelMapper.map(item, OrderItemDTO.class)));

        //Setting address in the OrderDTO
        orderDTO.setAddressId(addressId);
        //this returned orderDTO will make use of placeorder in OrderController
        return orderDTO;

    }
}
