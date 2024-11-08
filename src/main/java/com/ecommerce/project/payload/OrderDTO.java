package com.ecommerce.project.payload;

import com.ecommerce.project.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
//OredrDTO is a DTO that represents a single order.
public class OrderDTO
{
    private Long orderId;
    private String email;
    //this represents all the order items that exist in this particular order.
    private List<OrderItemDTO> orderItems;
    private LocalDate orderDate;
    //PaymentDTO represents payments a single payment
    private PaymentDTO payment;
    private Double totalAmount;
    private String orderStatus;
    private Long addressId;

}
