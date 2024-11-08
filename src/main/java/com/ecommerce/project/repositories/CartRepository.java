package com.ecommerce.project.repositories;

import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart , Long>
{
    //This annotation comes from JPA Repository
    //This annotation can do query here
    //? is the parameter we can specify number here 1 means 1st parameter.
    @Query("SELECT c FROM Cart c WHERE c.user.email = ?1")
    Cart findCartByEmail(String email);

   @Query("SELECT c FROM Cart c WHERE c.user.email = ?1 AND  c.id = ?2")
    Cart findCartByEmailAndCartId(String emailId, Long cartId);

   @Query("SELECT c FROM Cart c JOIN FETCH c.cartItems ci JOIN FETCH ci.product p WHERE p.id = ?1")
    List<Cart> findCartsByProductId(Long productId);

}
