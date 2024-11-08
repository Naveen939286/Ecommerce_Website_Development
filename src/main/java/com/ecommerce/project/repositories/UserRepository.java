package com.ecommerce.project.repositories;

import com.ecommerce.project.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long>
{
    //Return type of the method is optional
    Optional<User> findByUserName(String username);

    //Exists means the JPA is checking for something
    //checking is there any record with the username.
    //JPA converts the method into the queries
    Boolean existsByUserName(String username);

    Boolean existsByEmail(String email);
}
