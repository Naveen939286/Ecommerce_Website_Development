package com.ecommerce.project.controller;

import com.ecommerce.project.model.AppRole;
import com.ecommerce.project.model.Role;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repositories.RoleRepository;
import com.ecommerce.project.repositories.UserRepository;
import com.ecommerce.project.security.jwt.JwtUtils;
import com.ecommerce.project.security.request.LoginRequest;
import com.ecommerce.project.security.request.SignupRequest;
import com.ecommerce.project.security.response.MessageResponse;
import com.ecommerce.project.security.response.UserInfoResponse;
import com.ecommerce.project.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
//In this url we have all the authentication API's
@RequestMapping("/api/auth")
//This Class will Manage the Authentication Related Tasks
public class AuthController {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    //Sign in End Point
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        //    Creating Authentication object that exists in spring security.
        Authentication authentication;
        try {
            //Using AuthenticationManager to Authenticate the user.
            authentication = authenticationManager
                    //create the token with user name and password and then authenticating
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }


        //setting authentication object in security context holder
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //UserDetailsimpl is interface that Gives core user info here we use our own implementation.
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        //Using jwtutils to generate a token from username
//        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        //Here we make use of cookie
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        //Then we get list of roles because we need this pass in the response.
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        //Creating login response object with the parameters and we send the response status along with this response.
        //By this only we can get output in the post man
        //We delete jwttoken so we have response without JWT token
        //jwtCookie.toString() will give you the token in the string format instead of NULL.
        UserInfoResponse response = new UserInfoResponse(userDetails.getId(),
                userDetails.getUsername(), roles, jwtCookie.toString());

        //here we need to pass in the header first
        //here we make use of cookie
        //Setting a cookie while returning a response.
        //Setting the jwt cookie as a header
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                jwtCookie.toString())
                .body(response);

//        return ResponseEntity.ok(response);
    }

//----------------------
//1 st we are authenticating if the authentication valid.
// We set the context and then with the help of user details we are generating the Jwt token.
//Jwt token is only generated if the user is authenticated
//Then we get list of roles because we need this pass in the response.


    //signup page
    @PostMapping("/signup")
    //SignupRequest is a DTO. This is how SignupRequest is sent to the application
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        //Before we are creating a user we need to check the user name is already exists in it before if exits we through a error
        //This is what we are doing here.
        //userRepository we have our own DB make use of Authenticate users.
        //userRepository is the repository that helps us to talk to the user model.
        if (userRepository.existsByUserName(signupRequest.getUserName()))
        {
            //with in the body we create the instance of MessageResponse which says user name is already taken.
            //MessageResponse is a DTO that converts text based messages that we want to communicate to end users in JSON format.
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken"));
        }

        //Validating the email is already exists
        if (userRepository.existsByEmail(signupRequest.getEmail()))
        {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use"));
        }

        //if the user name and email is unique then we create user.
        //Creating the new User's Account
        //we are passing in user entity that we get every thing from the request.
        User user = new User(signupRequest.getUserName(),
                signupRequest.getEmail(),
                //Using the password Encoder the password is encoded.
                encoder.encode(signupRequest.getPassword()));

        //Here we create a role object here we get all the roles from the request.
        //This will store the role names that are provided in the Registration Request.
        Set<String> strRoles = signupRequest.getRole();

        //Creating one more role object which hold role entities to be assigned to the new user.
        //This is the one that goes to the DB.
        //User object needs a set of roles. So we create.
        Set<Role> roles = new HashSet<>();

        //Here To map the roles that we are getting from the request into the role that we are storing into the DB.
        if (strRoles == null) {
            //if the strRoles is null then we are assigning the default role here i.e user role.
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    //is the role not found in the DB we are throwing an exception.
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
            //Saving the userRole in the roles.
            roles.add(userRole);
        }
        //if the user has pass the role
        else {
            //Here we are iterating throw the strRoles
            //for every role we are mapping the corresponding role entity with the help of switch.
            //We can send multiple roles at a time i.e one user can sent multiple roles.
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        //fetching role from the repository and if the role does not exist throw error.
                        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        roles.add(adminRole);
                        break;

                    case "seller":
                        Role modRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        roles.add(modRole);
                        break;

                    default:
                        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        roles.add(userRole);
                }
            });
        }
        //setting the roles
        user.setRoles(roles);
        //save in the DB
        userRepository.save(user);
        //returning the confirmation to the user
        return ResponseEntity.ok(new MessageResponse("User Registered Successfully"));
    }

    @GetMapping("/username")
    //This method is going to return a UserName in the form of String.
    //This method gives the current user who logged in.
    public String currentUserName(Authentication authentication)
    {
        if (authentication != null)
        {
            return authentication.getName();
        }
        else
        {
            //else return a empty string.
            return "";
        }
    }

    //This method gives the current user details
    @GetMapping("/user")
    public  ResponseEntity<?> getUserDetails(Authentication authentication)
    {

        //we need a object of UserDetailsImpl to show the user details.
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        //Then we get list of roles because we need this pass in the response.
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        //Creating login response object with the parameters and we send the response status along with this response.
        //By this only we can get output in the post man
        UserInfoResponse response = new UserInfoResponse(userDetails.getId(),
                userDetails.getUsername(), roles );

        //here we need to pass in the body

        return ResponseEntity.ok().body(response);

    }

    //Sign out means we are clear the cookie right now has the JWT Info.
    //Invalidating the session
    @PostMapping("/signout")
    public ResponseEntity<?> signoutUser()
    {
        //By calling this cookie we are clear the cookie
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        //set the cookie
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                cookie.toString())
                .body(new MessageResponse("Signed out successfully"));
    }


}



