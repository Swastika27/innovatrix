package com.innovatrix.ahaar.exception;

import com.innovatrix.ahaar.model.APIResponse;
import com.innovatrix.ahaar.util.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {



    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<APIResponse<Void>> handleDataFoundException(NoDataFoundException ex) {
        System.out.println("Handling NoDataFoundException: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseBuilder.error(
                        HttpStatus.NOT_FOUND.value(),
                        "An error occurred: " + ex.getMessage(),
                        null));

    }

     @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<Void>> handleGlobalException(Exception ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseBuilder.error(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "An error occurred: " + ex.getMessage(),
                        null));
    }
}

