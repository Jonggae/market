package com.example.market.handler;

import com.example.market.exception.NotFoundProductException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Controller 레벨에서 발생하는 경우 이곳에 넣음.
@RestControllerAdvice
public class GlobalExceptionHandler {

}
