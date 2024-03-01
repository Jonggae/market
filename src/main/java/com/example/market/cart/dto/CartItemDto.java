package com.example.market.cart.dto;

import com.example.market.cart.entity.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {

    private Long itemId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double price;

    public static CartItemDto from(CartItem cartItem) {
        return CartItemDto.builder()
                .itemId(cartItem.getId())
                .productId(cartItem.getProduct().getId())
                .productName(cartItem.getProduct().getProductName())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getTotalPrice())
                .build();
    }
    @Override
    public String toString() {
        return "CartItemDto{" +
                "itemId=" + itemId +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';

}}
