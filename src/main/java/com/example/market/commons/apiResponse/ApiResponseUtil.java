package com.example.market.commons.apiResponse;

import org.springframework.http.ResponseEntity;

public class ApiResponseUtil {

    public static <T> ResponseEntity<ApiResponseDto<T>> successResponse(String message, T data) {
        ApiResponseDto<T> response = new ApiResponseDto<>(message, data);
        return ResponseEntity.ok(response);
    }
    public static <T> ResponseEntity<ApiResponseDto<T>> successResponseString(String message) {
        ApiResponseDto<T> response = new ApiResponseDto<>(message);
        return ResponseEntity.ok(response);
    }

    public static ResponseEntity<ApiResponseDto<String>> errorResponse(String message) {
        ApiResponseDto<String> response = new ApiResponseDto<>(message, null);
        return ResponseEntity.badRequest().body(response);
    }
}
