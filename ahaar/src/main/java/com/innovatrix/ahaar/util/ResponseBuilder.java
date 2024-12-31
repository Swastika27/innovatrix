package com.innovatrix.ahaar.util;

import com.innovatrix.ahaar.model.APIResponse;

public class ResponseBuilder {

    public static <T> APIResponse<T> success(String message, T data) {
        return new APIResponse<>("success", message, data);
    }

    public static <T> APIResponse<T> error(String message, T data) {
        return new APIResponse<>("error", message, data);
    }
}

