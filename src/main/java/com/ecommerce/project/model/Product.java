package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
@ToString
public class Product
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;

    @NotBlank
    @Size(min = 3, message = "Product Name must contains atleast 3 characters")
    private String productName;
    private String image;

    @NotBlank
    @Size(min = 6, message = "Product Description is alteast 6 characters")
    private String description;
    private Integer quantity;
    private double price; //100
    private double discount; //25
    private double specialPrice; //75


    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    //Mapping product to the user
    @JoinColumn(name = "seller_id")
    private User user;

    //---- Relation B/W Product and CartItem -----
    //This side we managed like a list of CartItems
    //This is one to many relation
    //field name in CartItem
    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    //list of cart items
    private List<CartItem> products = new ArrayList<>();

}



//--------------------------------------------------Note--------------------------------------------------------
//Where ever we see the JoinColumn Annotation that particular column will managing the relationship.
//Here category id of category managing every thing
//owner class lo join column



//joincolumn vunta mappedby vundadu and vice versa