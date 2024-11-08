package com.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//This is the response class for exception
//What are the exception made in future we can make use of this APIException class
//This is the response class used to pass any sort of message and status to the user.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class APIResponse
{
 public String message;
 private boolean status;
}
