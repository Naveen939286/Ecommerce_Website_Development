package com.ecommerce.project.security;

import com.ecommerce.project.model.AppRole;
import com.ecommerce.project.model.Role;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repositories.RoleRepository;
import com.ecommerce.project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ecommerce.project.security.jwt.AuthEntryPointJwt;
import com.ecommerce.project.security.jwt.AuthTokenFilter;
import com.ecommerce.project.security.services.UserDetailsServiceImpl;

import java.util.Set;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity
public class WebSecurityConfig
{
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    //Here we are returning the new instance of auth token filter
    //This AuthTokenFilter will intercepting the request and check for valid JWT token.
    public AuthTokenFilter authenticationJwtTokenFilter()
    {
        return new AuthTokenFilter();
    }


    @Bean
    //We are need this because to make use of custom UserDetailsService
    //This Bean that configures and returns the DaoAuthenticationFilterProvider
    public DaoAuthenticationProvider authenticationProvider()
    {
        //DaoAuthenticationFilterProvider is used to authenticate the users.
        //Here we are creating object for DaoAuthenticationFilterProvider
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        //Setting our own provider
        //userDetailsService is or provider.
        authProvider.setUserDetailsService(userDetailsService);
        //Setting the password encoder here.
        //Custom password encoder
        //All the changes are implemented here
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }


    //Getting the Authentication manager pin here using the AuthenticationConfiguration
    @Bean
    //Authentication manager is responsible for managing the authentication
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception
    {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    //In future if we change the BCryptPasswordEncoder we can write our own code here
    //Returning the bean of password encoder
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }



    @Bean
    //This method is configuring the filter chain.
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
    {
        //Disabling the CSRF protection since we are using of JWT which is Stateless
        http.csrf(csrf -> csrf.disable())
                //Setting Auth entry point here
                //Here we are telling Exception Handling should be done by unauthorized handler.
                //unauthorized Handler is the instance of auth entry pointJWT
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                //Here we are setting session policy to stateless we don't want any sessions
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //Here Configuring the request based on authorization.
                .authorizeHttpRequests(auth ->
                        //Here we uses requestMatches with auth object
                        // What requestMatches did is allow the public access to all the endpoints starting with auth
                        //auth meaning is sign in / sign up those kind of pages are public
                        auth.requestMatchers("/api/auth/**").permitAll()
                                //permitting any end point with API docs
                                .requestMatchers("/v3/api-docs/**").permitAll()
                                //since we are using H2 DataBase so we skip authentication for DB also
                                .requestMatchers("/h2-console/**").permitAll()
                                //admin can not be exposed so we need to hide
                               // .requestMatchers("/api/admin/**").permitAll()
                              //  .requestMatchers("/api/public/**").permitAll()
                                //here we permit swagger.
                                // Swagger is something that make use of to create API Documentation.
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/api/test/**").permitAll()
                                .requestMatchers("/images/**").permitAll()
                                //Any other request apart from the above are authenticated.
                                .anyRequest().authenticated()
                );

        //Here we are setting the authenticationProvider
        http.authenticationProvider(authenticationProvider());


        //Here we are adding this filter before the UsernamePasswordAuthenticationFilter
        http.addFilterBefore(authenticationJwtTokenFilter(),
                UsernamePasswordAuthenticationFilter.class);

        //Here we are enable the frames. When we try to access the h2 console.
        http.headers(headers -> headers.frameOptions(
                frameOptions -> frameOptions.sameOrigin()
        ));

        //returning the object of SecurityFilterChain type
        return http.build();
    }


    //With the help of this bean we are configuring global security settings.
    //Here we are excluding specified paths from the security filtering
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer()
    {
        //Here we actually ignoring these URL's from the security configuration.
        //Any path starting or having this path are excluded from the security filter chain.
        //Here we are doing this in the global level
        return (web -> web.ignoring().requestMatchers("/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**"));
    }

    //Here i'm adding a bean that is responsible for initializing the data.
    //Instead we use H2 DB but for every restart it clears the data.
    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository,
                                      UserRepository userRepository,
                                      PasswordEncoder passwordEncoder)
    {
        return args ->
        {
            //Retrieve or create roles
            //1st we are trying to get a role with the role user if the role does not exist we create it in DB. And Fetching it.
            // Same above process for seller and admin
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseGet(() -> {
                        Role newUserRole = new Role(AppRole.ROLE_USER);
                        return roleRepository.save(newUserRole);
                    });

            Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                    .orElseGet(() -> {
                        Role newSellerRole = new Role(AppRole.ROLE_SELLER);
                        return roleRepository.save(newSellerRole);
                    });

            Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                    .orElseGet(() -> {
                        Role newAdminRole = new Role(AppRole.ROLE_ADMIN);
                        return roleRepository.save(newAdminRole);
                    });

            //Creating a set of userroles,sellerrole,adminrole
            Set<Role> userRoles = Set.of(userRole);
            Set<Role> sellerRoles = Set.of(sellerRole);
            //adminRole is the set of all 3 roles
            Set<Role> adminRoles = Set.of(userRole, sellerRole, adminRole);


            //Creating the users if not already present
            //if the user is not present in DB.
            if(!userRepository.existsByUserName("user1"))
            {
                User user1 = new User("user1","user1@example.com",
                        passwordEncoder.encode("password1"));
                //Saving the users in DB
                userRepository.save(user1);
            }

            if(!userRepository.existsByUserName("seller1"))
            {
                User seller1 = new User("seller1","seller1@example.com",
                        passwordEncoder.encode("password2"));
                userRepository.save(seller1);
            }

            if (!userRepository.existsByUserName("admin"))
            {
                User admin = new User("admin","admin@example.cpm",
                        passwordEncoder.encode("password3"));
                userRepository.save(admin);
            }

            //After creating the users we are updating the roles of existing users
            userRepository.findByUserName("user1").ifPresent(user -> {
                //here we are updating the user with corresponding role.
                user.setRoles(userRoles);
                userRepository.save(user);
            });

            userRepository.findByUserName("seller1").ifPresent(seller -> {
                seller.setRoles(sellerRoles);
                userRepository.save(seller);
            });

            userRepository.findByUserName("admin").ifPresent(admin -> {
                admin.setRoles(adminRoles);
                userRepository.save(admin);
            });
        };

        //1st create the roles or retrive
        //2nd create set of role object
        //3rd create the users
        //4th we update the users to include the roles
    }


}