package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//order class will represent the entire order
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long orderId;

    @Email
    @Column(nullable = false)
    //email tells that which user does this order
    private String email;


    @OneToMany(mappedBy = "order", cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private List<OrderItem> orderItems = new ArrayList<>();


    private LocalDate orderDate;
    //Payment details
    //We also have payment information that link to the payment through join column
    //order is associated with payment object
    //one to one because one order will be paid once
    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    private Double totalAmount;
    private String orderStatus;

    //Reference to Address
    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

}
