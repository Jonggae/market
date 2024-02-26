package com.example.market.etc.apiResponse;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ApiResponseDto {

    private String message;
    public ApiResponseDto(String message) {
        this.message = message;
    }
}
