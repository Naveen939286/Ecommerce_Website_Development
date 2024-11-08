package com.ecommerce.project.exceptions;

import com.ecommerce.project.payload.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
//With the help of the RestControllerAdvice we make the class as global exception
public class MyGlobalExceptionHandler
{
    //We write all logic here to have a custom error exception
    //Any Exception came in our project this class will handle it.


    //We are telling Spring boot that i have a Exceptional handler for this kind of exception.
    //Whenever a exception of this kind occurs you need to intercept catch and you need to execute the method.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> myMethodArgumentNotValidException(MethodArgumentNotValidException e)
    {
        //We are accepting the argument of exception object
        //We are creating a object  of hashmap
        //We are going to have two Strings that is going to be returned one is going to store the field name and another one is message.
        //Next we a using the getBindResult and getAllErrors methods  on the exception object.
        //This method we are using with exception object
        //This method will retrieve a list of all the errors that are caught during the validation.
        //For each exception we are getting the field name and the message
        //And after getting field name and message we are adding to the response i.e we are adding it to the hash map
        //At last we are returning the response.
        //We are using lambda expression in for each loop
        //Bit the problem is we are getting 200 OK even we get exception we get 200OK Which not good
        //The above pblm will solved with the ResponseEntity.
        Map<String, String> response = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(err -> {
            String fieldName = ((FieldError)err).getField();
            String message = err.getDefaultMessage();
            response.put(fieldName,message);
        });
        //Here we are creating a new instance of response entity of type map.
        return new ResponseEntity<Map<String, String>>(response,
                HttpStatus.BAD_REQUEST);
    }

    //The Class not found so we can create that class
    @ExceptionHandler(ResourceNotFoundException.class)
    //This class returning string instead we add a structure for that we created the APIResponse class. So we change String return type to APIResponse
    public ResponseEntity<APIResponse> myResourceNotFoundException(ResourceNotFoundException e)
    {
        //Here i'm getting message from the exception object that i have access
        //String message = e.getMessage();
        //Creating a local variable message. And getting the responce and saving in message and returning.
        String message = e.getMessage();
        //We are putting false because there was a exception right
        APIResponse apiResponse = new APIResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    //What are the exception made in future we can make use of this APIException class

    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> myAPIException(APIException e)
    {
        String message = e.getMessage();
        APIResponse apiResponse = new APIResponse(message, false);
        //Bad request we are passing that already exists
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }
}
