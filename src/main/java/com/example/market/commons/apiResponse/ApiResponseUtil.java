package com.example.market.commons.apiResponse;

import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Builder
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

    public static <T> ResponseEntity<ApiResponseDto<T>> success(String message, T data, int statusCode) {
        ApiResponseDto<T> response = ApiResponseDto.<T>builder()
                .success(true)
                .message(message)
                .statusCode(statusCode)
                .timeStamp(LocalDateTime.now())
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    public static <T> ResponseEntity<ApiResponseDto<T>> error(String message, int statusCode, String errorCode, Object errorDetails) {
        ApiResponseDto<T> response = ApiResponseDto.<T>builder()
                .success(false)
                .message(message)
                .statusCode(statusCode)
                .errorCode(errorCode)
                .errorDetails(errorDetails)
                .timeStamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.valueOf(statusCode));
    }
}
