package com.innovatrix.ahaar.exception;

import com.innovatrix.ahaar.model.APIResponse;
import com.innovatrix.ahaar.util.ResponseBuilder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<APIResponse<Void>> handleDataIntegrityViolationException(WebRequest request, DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseBuilder.error(HttpStatus.CONFLICT.value(), "Data integrity violated", null));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<APIResponse<Void>> handleHttpMessageNotReadableException(WebRequest request, HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseBuilder.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<APIResponse<ArrayList<String>>> handleInvalidArgumentException(BindException ex, WebRequest request) {
        ArrayList<String> errorMessages = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errorMessages.add(error.getField() + ": " + error.getDefaultMessage());
        }

        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errorMessages.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseBuilder.error(HttpStatus.BAD_REQUEST.value(), "Invalid arguments", errorMessages));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<APIResponse<Void>> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseBuilder.error(HttpStatus.NOT_FOUND.value(), "User not found", null));

    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<APIResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseBuilder.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null));

    }

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
//        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseBuilder.error(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "An error occurred: " + ex.getMessage(),
                        null));
    }
}

