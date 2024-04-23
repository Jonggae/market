package com.example.market.order.controller;

import com.example.market.commons.MessageUtil;
import com.example.market.commons.apiResponse.ApiResponseDto;
import com.example.market.commons.apiResponse.ApiResponseUtil;
import com.example.market.order.dto.OrderDto;
import com.example.market.order.dto.OrderItemDto;
import com.example.market.order.dto.OrderStatusUpdateDto;
import com.example.market.order.message.OrderApiMessage;
import com.example.market.order.service.OrderService;
import com.example.market.security.utils.SecurityUtil;
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
    private final SecurityUtil securityUtil;

    @GetMapping("/admin/orders")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponseDto<List<OrderDto>>> getAllOrders() {
        List<OrderDto> orders = orderService.findAllOrders();
        return ApiResponseUtil.success("success", orders, 200);
    }

    // 내 주문 조회
    @GetMapping("/my-order")
    public ResponseEntity<ApiResponseDto<List<OrderDto>>> getOrderList(Authentication authentication) {
        String customerName = authentication.getName();
        Long customerId = securityUtil.getCurrentCustomerId(authentication);
        List<OrderDto> orderDto = orderService.getOrderList(customerId);
        String message = MessageUtil.getFormattedMessage(OrderApiMessage.ORDER_LIST_SUCCESS, customerName);
        return ApiResponseUtil.success(message, orderDto, 200);
    }

    // 주문 항목 추가
    @PostMapping("/my-order/items")
    public ResponseEntity<ApiResponseDto<OrderItemDto>> addOrderItem(Authentication authentication,
                                                                     @RequestBody OrderItemDto orderItemDto) {
        Long customerId = securityUtil.getCurrentCustomerId(authentication);
        OrderItemDto updatedItemDto = orderService.addOrderItem(customerId, orderItemDto);
        String message = MessageUtil.getMessage(OrderApiMessage.ORDER_ADD_SUCCESS);
        return ApiResponseUtil.success(message, updatedItemDto, 200);

    }

    // 주문 확정
    @PostMapping("/my-order/{orderId}/confirm") // 해당 유저의 주문번호를 지정하여 확정
    public ResponseEntity<ApiResponseDto<OrderDto>> confirmOrder(Authentication authentication, @PathVariable Long orderId) {
        Long customerId = securityUtil.getCurrentCustomerId(authentication);
        OrderDto confirmOrder = orderService.confirmOrder(customerId, orderId);
        String message = MessageUtil.getMessage(OrderApiMessage.ORDER_STATUS_UPDATE_SUCCESS);

        return ApiResponseUtil.success(message, confirmOrder, 200);
    }

    // 주문 상태 업데이트
    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponseDto<OrderDto>> updateOrderStatus(@PathVariable Long orderId,
                                                                      @RequestBody OrderStatusUpdateDto statusUpdateDto) {
        OrderDto updatedOrder = orderService.updateOrderStatus(orderId, statusUpdateDto);
        String message = MessageUtil.getMessage(OrderApiMessage.ORDER_STATUS_UPDATE_SUCCESS);
        return ApiResponseUtil.success(message, updatedOrder, 200);
    }

    // 주문 항목 수량 변경
    @PutMapping("/my-order/items/{orderItemId}")
    public ResponseEntity<ApiResponseDto<List<OrderDto>>> updateOrderItemQuantity(Authentication authentication, @PathVariable Long orderItemId,
                                                                                  @RequestBody OrderItemDto orderItemDto) {
        Long customerId = securityUtil.getCurrentCustomerId(authentication);
        List<OrderDto> updatedOrders = orderService.updateOrderItemQuantity(customerId, orderItemId, orderItemDto);
        String message = MessageUtil.getMessage(OrderApiMessage.ORDER_UPDATE_SUCCESS);
        return ApiResponseUtil.success(message, updatedOrders, 200);

    }

    // 주문 항목 삭제
    @DeleteMapping("/my-order/items/{orderItemId}")
    public ResponseEntity<ApiResponseDto<List<OrderDto>>> deleteOrderItem(@PathVariable Long orderItemId, Authentication authentication) {
        Long customerId = securityUtil.getCurrentCustomerId(authentication);
        List<OrderDto> orderDto = orderService.deleteOrderItem(customerId, orderItemId);
        String message = MessageUtil.getMessage(OrderApiMessage.ORDER_DELETE_SUCCESS);
        return ApiResponseUtil.success(message, orderDto, 200);

    }

    // 주문 삭제 ?? 이건 일단 놔돔. 로직을 다시 짜봐야 할듯. 필요한 로직인가?
    // 주문 취소의 역할을 할 수 있을듯
    @DeleteMapping("/{customerId}/{orderId}")
    public ResponseEntity<List<OrderDto>> deleteOrder(@PathVariable Long orderId, @PathVariable Long customerId) {
        List<OrderDto> orderDto = orderService.deleteOrder(orderId, customerId);
        return ResponseEntity.ok(orderDto);
    }
}
