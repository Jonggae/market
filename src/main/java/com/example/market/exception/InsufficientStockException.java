package com.example.market.exception;

public class InsufficientStockException extends RuntimeException{
    public InsufficientStockException(String productName) {
        super(productName + " - 상품의 재고가 부족합니다");
    }
}
