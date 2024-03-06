package com.example.market.exception;

public class NotFoundOrderItemException extends RuntimeException {
    public NotFoundOrderItemException () {
        super("내 주문에 해당 상품이 없습니다.");
    }
}
