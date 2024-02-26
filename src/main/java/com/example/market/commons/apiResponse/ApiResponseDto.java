package com.example.market.commons.apiResponse;

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
