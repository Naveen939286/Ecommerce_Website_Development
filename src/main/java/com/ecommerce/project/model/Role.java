package com.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

//This class will define the roles of the user.
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class Role
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    //This Particular mapping translated into toString so we exclude this field from two string method.
    @ToString.Exclude
    //By default if we persist enum type into the database it persisted as an integer.So convert into String
    @Enumerated(EnumType.STRING)
    @Column(length = 20, name = "role_name")
    //Approle is type of enum
    private AppRole roleName;

    //Adding a Constructor
    public Role(AppRole roleName)
    {
        this.roleName = roleName;
    }
}
