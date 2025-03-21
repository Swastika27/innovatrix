package com.innovatrix.ahaar.exception;

public class ResourceNotFoundException extends Exception{
    public ResourceNotFoundException(String message) {
        super(message + " : Resource not found");
    }
}
