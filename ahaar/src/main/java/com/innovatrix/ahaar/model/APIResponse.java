package com.innovatrix.ahaar.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class APIResponse<T> {
    private int statusCode; // HTTP status code
    private String responseMessage; // Status message (e.g., "Success", "Error")
    private T data; // The actual data (can be any type)

    // Constructor for just status code and message
    public APIResponse(int statusCode, String responseMessage) {
        this.statusCode = statusCode;
        this.responseMessage = responseMessage;
    }
}


