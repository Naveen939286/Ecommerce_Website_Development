package com.ecommerce.project.repositories;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>
{
    //JPA will provide implementation for without writing  a single line of code.
    // JPA will only trigger the query to DB on the method name itself.
    //By seeing the name of the method JPA knows the query should generate
    // it find the id with category and order the price in ascending order.
    Page<Product> findByCategoryOrderByPriceAsc(Category category, Pageable pageDetails);


    //Searching by product name by saying ignore the case
    //Like --> Pattern Matching
    Page<Product> findByProductNameLikeIgnoreCase(String keyword, Pageable pageDetails);
}
