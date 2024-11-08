package com.ecommerce.project.security.jwt;

import com.ecommerce.project.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component

//This is the utility class that contains utility methods for Generating,parsing and Validating the JWTs.
public class JwtUtils
{
    //logger is not mandatory we can skip this also.
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    //We are fetching this from application.properties
    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    //We are fetching this from application.properties
    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    //We are fetching this from application.properties
    @Value("${spring.ecom.app.jwtCookieName}")
    private String jwtCookie;

    //This method is for getting the JWT token from HTTP Header.
    //We getting a http request from header. Header name is Authorization
//    public String getJwtFromHeader(HttpServletRequest request)
//    {
//        //here we are passing bearer token with in the header itself.
//        String bearerToken = request.getHeader("Authorization");
//        logger.debug("Authorization Header: {}", bearerToken);
//        if (bearerToken != null && bearerToken.startsWith("Bearer "))
//        {
//            //Excluding the bearer space and getting the token.
//            return bearerToken.substring(7); // Remove Bearer prefix
//        }
//        //other wise we return null
//        return null;
//    }


    //----------------------------
    //Here we are getting the JWT from cookies
//here the request is incoming HTTP request that contains cookies.
//In this method we are extracting the HTTP request then we are fetching cookie.
    String getJwtFromCookies(HttpServletRequest request)
    {
        //Here we are getting the cookie.
        //This line retrieving the JWT cookie.
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        if(cookie != null)
        {
            //printing cookie
            //System.out.println("COOKIE: " + cookie.getValue());
            //if the cookie is not null then we are getting value from the cookie.
            return cookie.getValue();
        }
        else
        {
            return null;
        }
    }

    //Here in this method we are generating token from the username and returning as a cookie
    public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal)
    {
        //Here in this method we are generating token from the username and returning as a cookie
        String jwt = generateTokenFromUsername(userPrincipal.getUsername());
        // ResponseCookie creates a response cookie here and this creates using the JWT token.
        //And we restrict the cookie scope to /api end point
        //And we set maximum age of the cookie i.e expiration time of cookie.Here we set to 24 hrs.
        //Here we generate cookie with the cookie name and jwt token that we pass as a parameter
        ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwt).path("/api").
                maxAge(24*60*60)
                //Allowing client side scripts to access the cookie
                .httpOnly(false)
                //building the cookie and return that cookie.
                .build();
        return cookie;
    }

    //Clean the JWT cookie to sign out the user
    public ResponseCookie getCleanJwtCookie()
    {
        //Getting clean the cookie and return that cookie.
        ResponseCookie cookie = ResponseCookie.from(jwtCookie, null)
                .path("/api")
                .build();
        return  cookie;
    }


    //This method generating token from the User Details.
    public String generateTokenFromUsername(String username)
    {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }

    private Key key()
    {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    //This method validate the token
    public boolean validateJwtToken(String authToken) {
        try {
            System.out.println("Validate");
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}





























