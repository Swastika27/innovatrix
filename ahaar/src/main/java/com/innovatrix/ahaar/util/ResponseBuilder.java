package com.innovatrix.ahaar.util;

import com.innovatrix.ahaar.model.APIResponse;

public class ResponseBuilder {

    public static <T> APIResponse<T> success(int statusCode, String message, T data) {
        return new APIResponse<>(statusCode, message, data);
    }

    public static <T> APIResponse<T> error(int statusCode,String message, T data) {
        return new APIResponse<>(statusCode, message, data);
    }
}

