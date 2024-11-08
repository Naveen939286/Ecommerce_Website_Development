package com.ecommerce.project.security.response;

import java.util.List;

//In the response we will send username with JWT token and we will send list of roles.
//In future if we need any other items should be shown in the response we can add them here.
public class UserInfoResponse
{
    private Long id;
    private String jwtToken;

    private String username;

    //Here we are giving list of roles.
    private List<String> roles;

    public UserInfoResponse(Long id, String username, List<String> roles, String jwtToken)
    {
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.jwtToken = jwtToken;
    }

    public UserInfoResponse(Long id, String username, List<String> roles)
    {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}

