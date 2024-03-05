package com.example.market.cart.controller;

import com.example.market.cart.dto.CartDto;
import com.example.market.cart.dto.CartItemDto;
import com.example.market.cart.service.CartService;
import com.example.market.commons.apiResponse.ApiResponseDto;
import com.example.market.commons.apiResponse.ApiResponseUtil;
import com.example.market.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;
    private final CustomerService customerService; // authentication 에 들어있는 customerId 값을 얻기 위한 메서드 호출 용

    // 내 장바구니 조회
    @GetMapping("/my-cart")
    public ResponseEntity<ApiResponseDto<CartDto>> getCartItems(Authentication authentication) {
        String customerName = authentication.getName();
        Long customerId = customerService.findCustomerIdByAuthentication(authentication);
        CartDto cartDto = cartService.getCartItems(customerId);

        return ApiResponseUtil.successResponse(customerName + " 님의 장바구니 입니다.", cartDto);
    }

    // 내 장바구니 항목 추가
    @PostMapping("/my-cart/items")
    public ResponseEntity<ApiResponseDto<CartItemDto>> addCartItem(Authentication authentication, @RequestBody CartItemDto cartItemDto) {
        String customerName = authentication.getName();
        Long customerId = customerService.findCustomerIdByAuthentication(authentication);
        CartItemDto updatedCartItemDto = cartService.addCartItem(customerId, cartItemDto);

        return ApiResponseUtil.successResponse(customerName+ " 님의 장바구니에 담겼습니다.", updatedCartItemDto);
    }

    // 장바구니 항목 수정
    @PutMapping("/my-cart/items/{itemId}")
    public ResponseEntity<ApiResponseDto<CartDto>> updateCartItem(Authentication authentication,
                                                                  @PathVariable Long itemId, @RequestBody CartItemDto cartItemDto) {
        Long customerId = customerService.findCustomerIdByAuthentication(authentication);
        CartDto updatedCart = cartService.updateCartItem(customerId, itemId, cartItemDto);

        return ApiResponseUtil.successResponse("수량이 변경되었습니다.", updatedCart);
    }

    // 장바구니 항목 삭제
    @DeleteMapping("/my-cart/items/{itemId}")
    public ResponseEntity<ApiResponseDto<CartDto>> deleteCartItem(Authentication authentication, @PathVariable Long itemId) {
        Long customerId = customerService.findCustomerIdByAuthentication(authentication);
        CartDto updatedCart = cartService.deleteCartItem(customerId, itemId);

        return ApiResponseUtil.successResponse("상품이 삭제되었습니다.", updatedCart);
    }

    //장바구니 전체 비우기
    @DeleteMapping("/my-cart")
    public ResponseEntity<ApiResponseDto<String>> clearCart(Authentication authentication) {
        Long customerId = customerService.findCustomerIdByAuthentication(authentication);
        cartService.clearCart(customerId);
        return ApiResponseUtil.successResponseString("장바구니를 비웠습니다");
    }
}
