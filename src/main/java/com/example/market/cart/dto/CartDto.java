package com.example.market.cart.dto;

import com.example.market.cart.entity.Cart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {

    private Long customerId;
    private List<CartItemDto> cartItems;

    public static CartDto from(Cart cart) {
        return CartDto.builder()
                .customerId(cart.getCustomer().getId())
                .cartItems(cart.getItems().stream()
                        .map(CartItemDto::from)
                        .collect(Collectors.toList()))
        .build();

    }

}
