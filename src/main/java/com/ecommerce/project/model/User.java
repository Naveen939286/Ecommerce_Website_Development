package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//This class is used to track all the user information
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
//uniqueConstraints this constraint ensures that values in specifies columns are unique across the DB.
@Table(name = "users",
uniqueConstraints = {
        //UniqueConstraint on username and email
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
        })

public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    //Validations
    @NotBlank
    @Size(max = 20)
    @Column(name = "username")
    private String userName;

    @NotBlank
    @Size(max = 50)
    //Validated using email logic
    @Email
    @Column(name = "email")
    private String email;

    @NotBlank
    @Size(max = 120)
    @Column(name = "password")
    private String password;

    //Generating constructor for all except the userId
    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    //Corresponding setters and getters are automatically generated
    @Getter
    @Setter
    //This is having Many to many relationship between user and role
    //User can have multiple roles and role can be assigned to multiple users
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            //fetch type is eager whenever the user entity is retrieved from the DB its associated role entities are also loaded.
            fetch = FetchType.EAGER)

    //Name of join table is user_role which link the user and role entities
    @JoinTable(name = "user_role",
            //Joincolumn refers to the column in the join table
            //And that refers to the user entity which is user id that refers to the primary key of user entity where the user is ID
            joinColumns = @JoinColumn(name = "user_id"),
            //inverseJoinColumns that refers to the primary key of the role ID
            inverseJoinColumns = @JoinColumn(name = "role_id"))

    //Here we are represent roles in a hashset.
    private Set<Role> roles = new HashSet<>();


    @Getter
    @Setter
    //Users and Address one user have many address
    @OneToMany(mappedBy = "user",cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    //Join table name user_address
//    @JoinTable(name = "user_address",
//        joinColumns = @JoinColumn(name = "user_id"),
//        inverseJoinColumns = @JoinColumn(name = "address_id"))

    //We are refering to this address
    private List<Address> addresses = new ArrayList<>();

    //----Relation B/W User and Cart
    //I want to exclude this from toString so i add
    @ToString.Exclude
    //here it have one to one relation with cart in cart we have the user.
    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE,CascadeType.REMOVE})
    private Cart cart;



    //This Particular mapping translated into toString so we exclude this field from two string method.
    @ToString.Exclude
    //User as a Seller
    //OneToMany where one user can have multiple products to sell
    @OneToMany(mappedBy = "user",
            cascade = {CascadeType.PERSIST,CascadeType.MERGE},
            //orphanRemoval is a attribute provided to us by JPA
            // orphanRemoval this will do if a user is deleted then all the products become orphan so this will remove the products also.
            orphanRemoval = true)
    private Set<Product> products;
}

//-------------------------------------
//User is the Owner of the RelationShip