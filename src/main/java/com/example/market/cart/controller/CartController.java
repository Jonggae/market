package com.example.market.cart.controller;

import com.example.market.cart.dto.CartDto;
import com.example.market.cart.dto.CartItemDto;
import com.example.market.cart.service.CartService;
import com.example.market.commons.apiResponse.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    // 내 장바구니 조회
    @GetMapping("/{customerId}")
    public ResponseEntity<CartDto> getCartItems(@PathVariable Long customerId) {
        CartDto cartDto = cartService.getCartItems(customerId);
        return ResponseEntity.ok(cartDto);
    }

    // 장바구니 항목 추가
    @PostMapping("/{customerId}/items")
    public ResponseEntity<ApiResponseDto> addCartItem(@PathVariable Long customerId, @RequestBody CartItemDto cartItemDto) {
        cartService.addCartItem(customerId, cartItemDto);
        ApiResponseDto apiResponseDto = new ApiResponseDto("상품이 장바구니에 담겼습니다");
        return ResponseEntity.ok(apiResponseDto);
    }

    // 장바구니 항목 수정
    @PutMapping("/{customerId}/items/{itemId}")
    public ResponseEntity<CartDto> updateCartItem(@PathVariable Long customerId, @PathVariable Long itemId, @RequestBody CartItemDto cartItemDto) {
        CartDto updatedCart = cartService.updateCartItem(customerId, itemId, cartItemDto);
        return ResponseEntity.ok(updatedCart);
    }

    // 장바구니 항목 삭제
//    @DeleteMapping("/items/{itemId}")
//    public ResponseEntity<ApiResponseDto> deleteCartItem(@PathVariable Long itemId) {
//        cartService.deleteCartItem(itemId);
//        ApiResponseDto apiResponseDto = new ApiResponseDto("삭제되었습니다.");
//        return ResponseEntity.ok(apiResponseDto);
//    }

    //장바구니 전체 비우기
    @DeleteMapping("{customerId}")
    public ResponseEntity<ApiResponseDto> clearCart(@PathVariable Long customerId) {
        cartService.clearCart(customerId);
        ApiResponseDto apiResponseDto = new ApiResponseDto("장바구니를 비웠습니다.");
        return ResponseEntity.ok(apiResponseDto);
    }

}
