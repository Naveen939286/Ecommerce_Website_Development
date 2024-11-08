package com.ecommerce.project.controller;

import com.ecommerce.project.payload.OrderDTO;
import com.ecommerce.project.payload.OrderRequestDTO;
import com.ecommerce.project.service.OrderService;
import com.ecommerce.project.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController
{
    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthUtil authUtil;



    @PostMapping("/order/users/payments/{paymentMethod}")
    //this will have the orders that we get from the customers
    public ResponseEntity<OrderDTO> orderProducts(@PathVariable String paymentMethod,
                                                  @RequestBody OrderRequestDTO orderRequestDTO)
    {
        //1. getting the logged in user mail id
        String emailId = authUtil.loggedInEmail();

        //2. we are getting all the things form the order request
        // creating a method called place order from order service
        // for placing the order we need all the information in the place order.
        // all the information pass to the place order method and that will return the OrderDTO object.
        // We are having the info in placeOrder method once the order is placed successful then we get the object of OrderDTO.

        OrderDTO order = orderService.placeOrder(
                emailId,
                orderRequestDTO.getAddressId(),
                paymentMethod,
                orderRequestDTO.getPgName(),
                orderRequestDTO.getPgPaymentId(),
                orderRequestDTO.getPgStatus(),
                orderRequestDTO.getPgResponseMessage()

        );

        //That order we pass as the response to the user.
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }
}
