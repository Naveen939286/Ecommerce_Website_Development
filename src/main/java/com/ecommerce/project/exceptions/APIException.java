package com.ecommerce.project.exceptions;

public class APIException extends RuntimeException {
    //provide unique identifier for the version of class
    //The private static final serialVersionUID is used in a custom exception
    // (or any serializable class) to maintain version consistency during serialization and deserialization. It ensures that the class definition remains compatible even if the class is modified.
    //When to use:
    //Always use it when implementing a Serializable class, especially for exceptions.
    //It's critical when you plan to send objects over a network or save them for later use.
    //When not to use:
    //If your class is not intended to be serialized (not implementing Serializable), you don’t need it.
    //Here we make use of this because the message every time is changes so we uses this.
    private static final long serialVersionUID = 1L;

    //No argument constructor
    public APIException() {
    }

    //Constructor with a message
    public APIException(String message) {
        //Message Passed to super class
        super(message);
    }


}
