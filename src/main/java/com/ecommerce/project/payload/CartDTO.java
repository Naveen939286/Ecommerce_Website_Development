package com.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

//This class helps us to manage the request responses
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO
{
    private Long cartId;
    private Double totalPrice = 0.0;
    //list of Products
    //This is a request response so we choose productDTO
    private List<ProductDTO> products= new ArrayList<>();
}
