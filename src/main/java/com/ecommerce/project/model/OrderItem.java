package com.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor

//order item will have the information about the individual products within the particular order.
//order items is the items that exists in the  order
public class OrderItem
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @ManyToOne
    //link to product with many to one i.e one order can have many products
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    //we are link order against the address. So that we know where this order is supposed to be shifted.
    @JoinColumn(name = "order_id")
    private Order order;

    private int quantity;
    private double discount;
    private double orderedProductPrice;
}
