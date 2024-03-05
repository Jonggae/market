package com.example.market.cart.dto;

import com.example.market.cart.entity.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {

    private Long itemId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Long price;

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
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItemDto that = (CartItemDto) o;
        return Objects.equals(itemId, that.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId);
    }
}
