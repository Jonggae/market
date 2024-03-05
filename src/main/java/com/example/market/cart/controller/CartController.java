package com.example.market.cart.controller;

import com.example.market.cart.dto.CartDto;
import com.example.market.cart.dto.CartItemDto;
import com.example.market.cart.service.CartService;
import com.example.market.commons.apiResponse.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    // 내 장바구니 조회
    @GetMapping("/my-cart")
    public ResponseEntity<ApiResponseDto<CartDto>> getCartItems(Authentication authentication) {
        String customerName = authentication.getName();
        Long customerId = cartService.findCustomerIdByCustomerName(customerName);

        CartDto cartDto = cartService.getCartItems(customerId);
        ApiResponseDto<CartDto> response = new ApiResponseDto<>(customerName + " 님의 장바구니 입니다", cartDto);
        return ResponseEntity.ok(response);
    }

    // 내 장바구니 항목 추가
    @PostMapping("/my-cart/items")
    public ResponseEntity<ApiResponseDto<CartItemDto>> addCartItem(Authentication authentication, @RequestBody CartItemDto cartItemDto) {
        String customerName = authentication.getName();
        Long customerId = cartService.findCustomerIdByCustomerName(customerName);

        CartItemDto updatedCartItemDto = cartService.addCartItem(customerId, cartItemDto);
        ApiResponseDto<CartItemDto> response = new ApiResponseDto<>(customerName + " 님의 장바구니에 담겼습니다.", updatedCartItemDto);
        return ResponseEntity.ok(response);
    }

    // 장바구니 항목 수정
    @PutMapping("/my-cart/items/{itemId}")
    public ResponseEntity<ApiResponseDto<CartDto>> updateCartItem(Authentication authentication,
                                                                  @PathVariable Long itemId, @RequestBody CartItemDto cartItemDto) {
        String customerName = authentication.getName();
        Long customerId = cartService.findCustomerIdByCustomerName(customerName);

        CartDto updatedCart = cartService.updateCartItem(customerId, itemId, cartItemDto);
        ApiResponseDto<CartDto> response = new ApiResponseDto<>("수량이 변경되었습니다", updatedCart);
        return ResponseEntity.ok(response);
        // 수량변경은 따로 안내메시지를 보내지 않고 업데이트된 장바구니를 그대로 보여줌
    }

    // 장바구니 항목 삭제
    @DeleteMapping("/my-cart/items/{itemId}")
    public ResponseEntity<ApiResponseDto<CartDto>> deleteCartItem(Authentication authentication, @PathVariable Long itemId) {
        String customerName = authentication.getName();
        Long customerId = cartService.findCustomerIdByCustomerName(customerName);

        CartDto updatedCart = cartService.deleteCartItem(customerId, itemId);
        ApiResponseDto<CartDto> response = new ApiResponseDto<>("상품이 삭제되었습니다.", updatedCart);
        return ResponseEntity.ok(response);
    }

    //장바구니 전체 비우기
    @DeleteMapping("/my-cart")
    public ResponseEntity<ApiResponseDto<String>> clearCart(Authentication authentication) {
        String customerName = authentication.getName();
        Long customerId = cartService.findCustomerIdByCustomerName(customerName);
        cartService.clearCart(customerId);
        ApiResponseDto<String> response = new ApiResponseDto<>("장바구니를 비웠습니다.");
        return ResponseEntity.ok(response);
    }

}
