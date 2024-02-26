package com.example.market.security.exception;

public class NotFoundProductException extends RuntimeException{

    public NotFoundProductException() {
        super("해당 상품은 존재하지 않습니다.");
    }
}
