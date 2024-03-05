package com.example.market.cart.service;

import com.example.market.cart.dto.CartDto;
import com.example.market.cart.dto.CartItemDto;
import com.example.market.cart.entity.Cart;
import com.example.market.cart.entity.CartItem;
import com.example.market.cart.repository.CartItemRepository;
import com.example.market.cart.repository.CartRepository;
import com.example.market.customer.entity.Customer;
import com.example.market.customer.repository.CustomerRepository;
import com.example.market.product.entity.Product;
import com.example.market.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    // 내 장바구니 조회 (장바구니 안의 상품을 보여줌)
    public CartDto getCartItems(Long customerId) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("장바구니가 없습니다"));

        List<CartItemDto> cartItems = cart.getItems().stream()
                .map(CartItemDto::from)
                .collect(Collectors.toList());

        return new CartDto(customerId, cartItems);
    }

    // 장바구니 항목 추가
    public CartItemDto addCartItem(Long customerId, CartItemDto cartItemDto) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> cartRepository.save(new Cart(customerId))); // 장바구니가 없다면 생성하는 것인데 없을 수가 있는가?

        Product product = productRepository.findById(cartItemDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));

        CartItem newCartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(cartItemDto.getQuantity())
                .build();

        return CartItemDto.from(cartItemRepository.save(newCartItem));
    }

        // 장바구니 항목 수정 - 수량
        public CartDto updateCartItem (Long customerId, Long itemId, CartItemDto cartItemDto){
            CartItem cartItem = cartItemRepository.findById(itemId)
                    .orElseThrow(() -> new IllegalArgumentException("장바구니 내의 해당 상품을 찾을 수 없습니다."));

            cartItem.setQuantity(cartItemDto.getQuantity());
            cartItemRepository.save(cartItem);

            return getCartItems(customerId);
        }

        // 장바구니 항목 삭제
        public CartDto deleteCartItem (Long customerId, Long itemId){
            CartItem cartItem = cartItemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("장바구니 내의 해당 상품을 찾을 수 없습니다."));
            cartItemRepository.deleteById(cartItem.getId());

            return getCartItems(customerId);
        }

        // 장바구니 전체 비우기
        @Transactional //Transactional 필요
        public void clearCart (Long customerId){
            Cart cart = cartRepository.findByCustomerId(customerId)
                    .orElseThrow(() -> new RuntimeException("장바구니가 없습니다"));
            cartItemRepository.deleteAllByCart(cart);
            CartDto.from(cart);
        }

        // 인증객체 내의 사용자 정보 가져오기 위해 customerId 찾기
        public Long findCustomerIdByCustomerName (String customerName){
            return customerRepository.findByCustomerName(customerName)
                    .map(Customer::getId)
                    .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));
        }

        public Long getCustomerId (Authentication authentication){
            String customerName = authentication.getName();
            return findCustomerIdByCustomerName(customerName);

        }
    }
