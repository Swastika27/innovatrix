package com.innovatrix.ahaar.exception;


public class NoDataFoundException extends RuntimeException {
    private String message;


    public NoDataFoundException(String msg) {
        super(msg);
        this.message = msg;
    }

    public String getMessage() {
        return message;
    }
}
