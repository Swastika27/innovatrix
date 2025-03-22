package com.innovatrix.ahaar.exception;

public class ResourceNotFoundException extends Exception{
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message + " : Resource not found");
    }

    public ResourceNotFoundException() {
        super("Resource not found");
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message + " : Resource not found", cause);
    }

    public ResourceNotFoundException(Throwable cause) {
        super("Resource not found", cause);
    }
}
