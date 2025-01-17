package com.innovatrix.ahaar.exception;


import lombok.Getter;

@Getter
public class NoDataFoundException extends RuntimeException {
    private String message;


    public NoDataFoundException(String msg) {
        super(msg);
        this.message = msg;
    }

}
