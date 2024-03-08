package com.example.market.commons.apiResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Builder
@AllArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값이 아닌 필드만 포함
public class ApiResponseDto<T> {

    private boolean success;
    private String message;
    private int statusCode;
    private T data;
    private String errorCode;
    private Object errorDetails;
    private LocalDateTime timeStamp;

    public ApiResponseDto(String message) {
        this.message = message;
    }

    public ApiResponseDto(String message, T data) {
        this.message = message;
        this.data = data;
    }
}
