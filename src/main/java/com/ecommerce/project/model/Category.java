package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.web.WebProperties;

import java.util.List;


@Entity(name = "categories")
//Data include @Getter, @Setter, @ToString, @EqualsAndHashCode and @RequiredArgsConstructor annotations
@Data
//@NoArgsConstructor will generate a constructor with no parameters.
@NoArgsConstructor
//@AllArgsConstructor generates a constructor with 1 parameter for each field in your class.
@AllArgsConstructor
public class Category
{
    //Attributes written here
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    //category name can not be blank now
    @NotBlank
    //Custom message
    @Size(min = 5 , message = "Category name must contain atleast 5 Characters")
    //Default message
    //@Size(min = 5)
    private String categoryName;


    //Constructor with both the parameters
//
//    public Category(Long category_id, String category_name) {
//        this.category_id = category_id;
//        this.category_name = category_name;
//    }
//
//    //Best Practice to add a default constructor
//    public Category() {
//
//    }
//
//    //Getters and Setters
//
//
//    public Long getCategory_id() {
//        return category_id;
//    }
//
//    public void setCategory_id(Long category_id) {
//        this.category_id = category_id;
//    }
//
//    public String getCategory_name() {
//        return category_name;
//    }
//
//    public void setCategory_name(String category_name) {
//        this.category_name = category_name;
//    }

    //Add the bidirectional relation ship in ProductServiceImpl we need products list.
    //We need product list there.
    //Mapped by product in Category.
    @OneToMany(mappedBy = "category" , cascade = CascadeType.ALL)
    private List<Product> products;
}
