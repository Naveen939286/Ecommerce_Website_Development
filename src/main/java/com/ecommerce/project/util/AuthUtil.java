package com.ecommerce.project.util;

import com.ecommerce.project.model.User;
import com.ecommerce.project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

//AuthUtil is a class that helps us to work with auth related tasks.
//it has some helper methods like getting the email of logged user
// and getting username of logged user.
//This method gives Authenticated user details.
//If any changes need in the authentication we just change in this class.

//@Comonent so spring will manage this class
@Component
public class AuthUtil
{
    @Autowired
    UserRepository userRepository;

    //Method that gives the email id of logged in user.
    public String loggedInEmail()
    {
        //getting instance of Authentication object
        //This authentication object will have the details of user who is currently authenticated.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //finding the user with the userRepository
        //authentication gives the user name and using userRepository we can find the user name in DB and store in user object.
        User user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User Not Found"));
        //returning the user email.
        return user.getEmail();
    }

    //This gives the logged in user id
    public Long loggedInUserId()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        return user.getUserId();
    }

    //This gives the logged in user object
    public User loggedInUser()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        return user;
    }

}
