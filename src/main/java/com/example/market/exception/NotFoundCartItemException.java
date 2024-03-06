package com.example.market.exception;

public class NotFoundCartItemException extends RuntimeException{
    public NotFoundCartItemException () {
        super("장바구니 내에서 상품을 찾을 수 없습니다.");
    }
}
