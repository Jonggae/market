package com.example.market.cart.repository;

import com.example.market.cart.entity.Cart;
import com.example.market.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCartId(Long cartId);

    Optional<CartItem> findByIdAndCart_CustomerId(Long itemId, Long customerId);

    void deleteAllByCart(Cart cart);
}
