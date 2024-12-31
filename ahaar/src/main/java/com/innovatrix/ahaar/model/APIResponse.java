package com.innovatrix.ahaar.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class APIResponse<T> {
    private String status; // e.g., "success" or "error"
    private String message; // A descriptive response message
    private T data; // Generic field to hold any type of data
}

