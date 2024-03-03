package com.example.market.commons.apiResponse;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ApiResponseDto<T> {

    private String message;
    private T data;

    public ApiResponseDto(String message) {
        this.message = message;
    }

    public ApiResponseDto(String message, T data) {
        this.message = message;
        this.data = data;
    }
}
