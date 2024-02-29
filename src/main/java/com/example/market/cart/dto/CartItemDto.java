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
    private Integer quantity;
    private Double price;

    public static CartItemDto from(CartItem cartItem) {
        return CartItemDto.builder()
                .itemId(cartItem.getId())
                .productId(cartItem.getProduct().getId())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getTotalPrice())
                .build();
    }

}
