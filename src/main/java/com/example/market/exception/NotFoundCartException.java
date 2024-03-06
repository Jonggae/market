package com.example.market.exception;

public class NotFoundCartException extends RuntimeException{
    public NotFoundCartException() {
        super("장바구니가 없습니다.");
    }
}
