package com.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "cart_items")
@NoArgsConstructor
@AllArgsConstructor
public class CartItem
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    //create a relationship with cart. It is a Two way relationship.
    //in cart it is one to many here it is many to one
    @ManyToOne
    //This is the owner of the relation ship we are specifying here.
    //cart id is unique identifier that present in carts and cart items
    @JoinColumn(name = "cart_id")
    private Cart cart;

    //We have many cart items
    @ManyToOne
    @JoinColumn(name = "product_id")
    //field name
    private Product product;

    private Integer quantity;
    private Double discount;
    private Double productPrice;

}
