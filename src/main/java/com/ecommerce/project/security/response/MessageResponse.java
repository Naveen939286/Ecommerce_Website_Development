package com.ecommerce.project.security.response;

//MessageResponse is used to Manage the response.
//This simply converts the string and represents it as JSON in the response.
public class MessageResponse
{
    private String message;
    public MessageResponse(String message)
    {
        this.message = message;
    }
    public String getMessage()
    {
        return message;
    }
    public void setMessage(String message)
    {
        this.message = message;
    }
}
