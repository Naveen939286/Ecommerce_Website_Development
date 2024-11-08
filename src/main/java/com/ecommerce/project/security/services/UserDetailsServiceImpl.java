package com.ecommerce.project.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.project.model.User;
import com.ecommerce.project.repositories.UserRepository;

//By using this custom interface will retrieve the UserDetails from the DB
@Service
//UserDetailsService is inbuilt which allows us to load the user data that is stored for our application.
//We customize the UserDetailsService in order to build the scalable application.
//The reason we customize in future if we need any changes we made from here.
public class UserDetailsServiceImpl implements UserDetailsService
{
    @Autowired
            //We fetch the user information using this repository so we autowire.
            //So we can get access to use the user Data.
    UserRepository userRepository;

    //this method is loadUserByUsername override this method is cmg from UserDetailsService

    @Override

    //This @Transactional annotation will provide secure and consistent way to fetch user data.
    //this annotation will make our method fully execute or not execute at all.
    @Transactional
    //Here we are providing our own implementation over here as how our application is supposed to be loaded.
    //Here we are accepting a username
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        //That username we nake use in the repository method which is of findByUserName
        User user = userRepository.findByUserName(username)
                //if the username not found we are throwing a exception
                //At final we are assigning the result to the model class User object user
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        //The user is been built and object of  UserDetails type is being returned.
        return UserDetailsImpl.build(user);
    }


}