package com.ecommerce.project.repositories;

import com.ecommerce.project.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CartItemRepository extends JpaRepository<CartItem , Long>
{
    //here we need product id and cart id
    //cart item give get access to cart so we took cart item.cart.id
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = ?1 AND ci.product.id=?2")
    CartItem findCartItemByProductIdAndCartId(Long cartId, Long productId);

    //This annotation will tell to JPA that we are intending to modify the DataBase with the help of this annotation.
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id =?1 AND ci.product.id = ?2")
    void deleteCartItemByProductIdAndCartId(Long cartId, Long productId);


}
