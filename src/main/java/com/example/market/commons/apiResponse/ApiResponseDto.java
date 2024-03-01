package com.example.market.commons.apiResponse;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ApiResponseDto {
    /*
    * apiResponse의 형태를 통일시켜서 전체적으로 사용할 수 있게 해보자
    *
     public class ApiResponseDto<T> {

    private String message;
    private T data;

    public ApiResponseDto(String message, T data) {
        this.message = message;
        this.data = data;
    }
}*/

    private String message;

    public ApiResponseDto(String message) {
        this.message = message;
    }
}
