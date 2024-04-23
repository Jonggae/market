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
    private Long totalPrice;

    public static CartDto from(Cart cart) {
        Long totalPrice = cart.getItems().stream()
                .mapToLong(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
        return CartDto.builder()
                .customerId(cart.getCustomer().getId())
                .cartItems(cart.getItems().stream()
                        .map(CartItemDto::from)
                        .collect(Collectors.toList()))
                .totalPrice(totalPrice)
                .build();

    }

}
