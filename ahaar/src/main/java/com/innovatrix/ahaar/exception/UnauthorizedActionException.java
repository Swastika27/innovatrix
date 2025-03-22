package com.innovatrix.ahaar.exception;

public class UnauthorizedActionException extends Exception {
    private static final long serialVersionUID = 1L;

    public UnauthorizedActionException() {
        super();
    }

    public UnauthorizedActionException(String message) {
        super(message);
    }

    public UnauthorizedActionException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedActionException(Throwable cause) {
        super(cause);
    }
}

