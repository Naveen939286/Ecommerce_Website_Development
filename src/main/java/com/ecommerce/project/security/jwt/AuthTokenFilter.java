package com.ecommerce.project.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//This class will intercepts every request to check if it is authenticated.
@Component
//OncePerRequestFilter this is a class provided by Spring Security.
//OncePerRequestFilter class makes sure that this particular filter executes only once per request.
public class AuthTokenFilter extends OncePerRequestFilter
{
    //Autowiring instance of jwtUtils and UserDetailsService

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    //logger is not mandatory we can skip this also.
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException
    {
        logger.debug("AuthTokenFilter called for URI: {}", request.getRequestURI());
        //For this returned jwt we will do validations by kept in try and catch blocks.
        try {
            //We are calling parseJWt method to extract the JWT Token.
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                //Authentication token created with the userDetails and Authorities.
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null,
                                userDetails.getAuthorities());

                //Just for Debugging purpose. Not mandatory
                logger.debug("Roles from JWT: {}", userDetails.getAuthorities());

                //Setting the details into the authentication token.
                //Here we are enhancing the authentication object.
                //Here we are enhancing this token with additional details that we are getting from the request.
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //Here we are taking the object of authentication  and setting the security context if effectively authenticating the user for the duration of the request.
                //once the validation is done we are creating the object and we are setting this in the security context.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        //Catch block to catch any sort of exception
        catch (Exception e)
        {
            logger.error("Cannot set user authentication: {}", e);
        }

        //Here we add the custom filter chain
        //This below line says continue the filter chain as usual.
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request)
    {

//Extracted token returned by getJwtFromHeader method in JwtUtils class.
//We store that extracted token in jwt and return.
//For this returned jwt we will do validations by kept in try and catch blocks from above.
        //This method is to pass the JWT
//        String jwt = jwtUtils.getJwtFromHeader(request);
//        logger.debug("AuthTokenFilter.java: {}", jwt);
//        return jwt;

        //here we are make use of cookies so method name changes
        String jwt = jwtUtils.getJwtFromCookies(request);
        logger.debug("AuthTokenFilter.java: {}", jwt);
        return jwt;
    }
}
















