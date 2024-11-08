package com.ecommerce.project.security.services;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ecommerce.project.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

@NoArgsConstructor
@Data

//Here we are customizing the User Details interface acc to the our requirement
public class UserDetailsImpl implements UserDetails
{
    //we create the serialVersionUID to ensure that the serialization consistency is there across the diff. JVM's.
    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String email;

    //@JsonIgnore is the ignored in the JSON output we don't want our password to go as the output.
    //Adding the annotation will ignore it during serialization to JSON
    @JsonIgnore
    private String password;

    // We added collection of Authorities i.e collection of roles and permissions that grant to the user.
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String username, String email, String password,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    //UserDetailsImpl this method converts the domain user object.
    //domain user object this is our model user
    //Domain user object the user from model converting into UserDetailsImpl type.
    public static UserDetailsImpl build(User user)
    {

//1st it get the all the list of authorities that the user has.
        List<GrantedAuthority> authorities = user.getRoles().stream()
                //2nd then we mapping the rule to simple granted authority type.
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                //3rd then we collecting them as a list.
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.getPassword(),
                authorities);
    }


    //Here we have all the overridden methods.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId()
    {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    //We have equality and hashcode check
    //equals this will compares the user by ID Attribute
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }

}