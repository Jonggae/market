package com.example.market.cart.controller;

import com.example.market.cart.dto.CartDto;
import com.example.market.cart.dto.CartItemDto;
import com.example.market.cart.message.CartApiMessage;
import com.example.market.cart.service.CartService;
import com.example.market.commons.MessageUtil;
import com.example.market.commons.apiResponse.ApiResponseDto;
import com.example.market.commons.apiResponse.ApiResponseUtil;
import com.example.market.security.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;
    private final SecurityUtil securityUtil;

    // 내 장바구니 조회
    @GetMapping("/my-cart")
    public ResponseEntity<ApiResponseDto<CartDto>> getCartItems(Authentication authentication) {
        Long customerId = securityUtil.getCurrentCustomerId(authentication);
        CartDto cartDto = cartService.getCartItems(customerId);
        String message = MessageUtil.getMessage(CartApiMessage.CART_LIST_SUCCESS);
        return ApiResponseUtil.success(message, cartDto, 200);
    }

    // 내 장바구니 항목 추가
    @PostMapping("/my-cart/items")
    public ResponseEntity<ApiResponseDto<CartItemDto>> addCartItem(Authentication authentication, @RequestBody CartItemDto cartItemDto) {
        Long customerId = securityUtil.getCurrentCustomerId(authentication);
        CartItemDto updatedCartItemDto = cartService.addCartItem(customerId, cartItemDto);
        String message = MessageUtil.getMessage(CartApiMessage.CART_ITEM_ADD_SUCCESS);
        return ApiResponseUtil.success(message, updatedCartItemDto, 200);
    }

    // 장바구니 항목 수정
    @PutMapping("/my-cart/items/{itemId}")
    public ResponseEntity<ApiResponseDto<CartDto>> updateCartItem(Authentication authentication,
                                                                  @PathVariable Long itemId, @RequestBody CartItemDto cartItemDto) {
        Long customerId = securityUtil.getCurrentCustomerId(authentication);
        CartDto updatedCart = cartService.updateCartItem(customerId, itemId, cartItemDto);
        String message = MessageUtil.getMessage(CartApiMessage.CART_ITEM_UPDATE_SUCCESS);
        return ApiResponseUtil.success(message, updatedCart, 200);
    }

    // 장바구니 항목 삭제
    @DeleteMapping("/my-cart/items/{itemId}")
    public ResponseEntity<ApiResponseDto<CartDto>> deleteCartItem(Authentication authentication, @PathVariable Long itemId) {
        Long customerId = securityUtil.getCurrentCustomerId(authentication);
        CartDto updatedCart = cartService.deleteCartItem(customerId, itemId);
        String message = MessageUtil.getMessage(CartApiMessage.CART_ITEM_DELETE_SUCCESS);
        return ApiResponseUtil.success(message, updatedCart, 200);
    }

    //장바구니 전체 비우기
    @DeleteMapping("/my-cart")
    public ResponseEntity<ApiResponseDto<String>> clearCart(Authentication authentication) {
        Long customerId = securityUtil.getCurrentCustomerId(authentication);
        cartService.clearCart(customerId);
        String message = MessageUtil.getMessage(CartApiMessage.CART_CLEAR_SUCCESS);
        return ApiResponseUtil.success(message, null, 200);
    }
}
