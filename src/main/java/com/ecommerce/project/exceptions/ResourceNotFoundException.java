package com.ecommerce.project.exceptions;

public class ResourceNotFoundException extends RuntimeException
{
    //Store resourcename that was not found
    String resourceName;
    String field;
    String fieldName;
    Long fieldId;
    //This custom exception provided constructor to set the resource name,field name and field id that are not found by extending the runtime exception.
    //Creating No argument Constructor


    public ResourceNotFoundException() {
    }

    //Cretaing constructor for all except id.
    public ResourceNotFoundException(String resourceName, String field, String fieldName) {
        //Here we pass this in a particular format
        //Using String formatting in java
        super(String.format("%s not found with %s: %s", resourceName, field, fieldName));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldName = fieldName;
    }

    //Cretaing constructor for all field name
    public ResourceNotFoundException(String resourceName, String field, Long fieldId) {
        //FieldId so we use %d
        super(String.format("%s not found with %s: %d", resourceName, field, fieldId));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldId = fieldId;
    }
}
