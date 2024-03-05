package com.example.market.order.controller;

import com.example.market.commons.apiResponse.ApiResponseDto;
import com.example.market.commons.apiResponse.ApiResponseUtil;
import com.example.market.customer.service.CustomerService;
import com.example.market.order.dto.OrderDto;
import com.example.market.order.dto.OrderItemDto;
import com.example.market.order.entity.Order.OrderStatus;
import com.example.market.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final CustomerService customerService;

    // 내 주문 조회
    @GetMapping("/my-order")
    public ResponseEntity<ApiResponseDto<List<OrderDto>>> getOrderList(Authentication authentication) {
        String customerName = authentication.getName();
        Long customerId = customerService.findCustomerIdByAuthentication(authentication);
        List<OrderDto> orderDto = orderService.getOrderList(customerId);

        return ApiResponseUtil.successResponse(customerName + " 님의 주문 목록 입니다.", orderDto);

    }

    // 주문 항목 추가
    @PostMapping("/my-order/items")
    public ResponseEntity<ApiResponseDto<OrderItemDto>> addOrderItem(Authentication authentication,
                                                                     @RequestBody OrderItemDto orderItemDto) {
        String customerName = authentication.getName();
        Long customerId = customerService.findCustomerIdByAuthentication(authentication);
        OrderItemDto updatedItemDto = orderService.addOrderItem(customerId, orderItemDto);

        return ApiResponseUtil.successResponse(customerName + " 님의 주문에 상품이 추가 되었습니다.", updatedItemDto);

    }

    // 주문 확정
    @PostMapping("/my-order/{orderId}/confirm") // 해당 유저의 주문번호를 지정하여 확정
    public ResponseEntity<ApiResponseDto<OrderDto>> confirmOrder(Authentication authentication, @PathVariable Long orderId) {
        Long customerId = customerService.findCustomerIdByAuthentication(authentication);
        OrderDto confirmOrder = orderService.confirmOrder(customerId, orderId);

        return ApiResponseUtil.successResponse("주문이 확정되었습니다. 결제를 진행해주세요",confirmOrder);
    }

    // 주문 상태 업데이트
    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponseDto<OrderDto>> updateOrderStatus(@PathVariable Long orderId,
                                                                      @RequestParam OrderStatus newStatus) {
        OrderDto updatedOrder = orderService.updateOrderStatus(orderId, newStatus);
        return ApiResponseUtil.successResponse("주문 상태가 업데이트 되었습니다", updatedOrder);

    }

    // 주문 항목 수량 변경
    @PutMapping("/my-order/items/{orderItemId}")
    public ResponseEntity<ApiResponseDto<List<OrderDto>>> updateOrderItemQuantity(Authentication authentication, @PathVariable Long orderItemId,
                                                                                  @RequestBody OrderItemDto orderItemDto) {
        Long customerId = customerService.findCustomerIdByAuthentication(authentication);
        List<OrderDto> updatedOrders = orderService.updateOrderItemQuantity(customerId, orderItemId, orderItemDto);
        return ApiResponseUtil.successResponse("주문 수량이 변경되었습니다", updatedOrders);

    }

    // 주문 항목 삭제
    @DeleteMapping("/my-order/items/{orderItemId}")
    public ResponseEntity<ApiResponseDto<List<OrderDto>>> deleteOrderItem(@PathVariable Long orderItemId, Authentication authentication) {
        Long customerId = customerService.findCustomerIdByAuthentication(authentication);
        List<OrderDto> orderDto = orderService.deleteOrderItem(customerId, orderItemId);
        return ApiResponseUtil.successResponse("주문할 상품이 삭제 되었습니다.", orderDto);

    }

    // 주문 삭제 ?? 이건 일단 놔돔. 로직을 다시 짜봐야 할듯. 필요한 로직인가?
    @DeleteMapping("/{customerId}/{orderId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<OrderDto>> deleteOrder(@PathVariable Long orderId, @PathVariable Long customerId) {
        List<OrderDto> orderDto = orderService.deleteOrder(orderId, customerId);
        return ResponseEntity.ok(orderDto);
    }
}
