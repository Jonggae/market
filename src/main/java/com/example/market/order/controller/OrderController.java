package com.example.market.order.controller;

import com.example.market.order.dto.OrderDto;
import com.example.market.order.dto.OrderItemDto;
import com.example.market.order.entity.Order.OrderStatus;
import com.example.market.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    // 내 주문 조회
    @GetMapping("/{customerId}")
    public ResponseEntity<List<OrderDto>> getOrderList(@PathVariable Long customerId) {
        List<OrderDto> orderDto = orderService.getOrderList(customerId);
        return ResponseEntity.ok(orderDto);
    }

    // 주문 항목 추가
    @PostMapping("/{customerId}/items")
    public ResponseEntity<OrderItemDto> addOrderItem(@PathVariable Long customerId,
                                                     @RequestBody OrderItemDto orderItemDto) {
        OrderItemDto newOrderItem = orderService.addOrderItem(customerId, orderItemDto);
        return ResponseEntity.ok(newOrderItem);
    }

    // 주문 확정
    @PostMapping("/{customerId}/{orderId}/confirm") // 해당 유저의 주문번호를 지정하여 확정
    public ResponseEntity<OrderDto> confirmOrder(@PathVariable Long customerId, @PathVariable Long orderId) {
        OrderDto confirmOrder = orderService.confirmOrder(customerId, orderId);
        return ResponseEntity.ok(confirmOrder);
    }

    // 주문 상태 업데이트
    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<OrderDto> updateOrderStatus(@PathVariable Long orderId,
                                                      @RequestParam OrderStatus newStatus) {
        OrderDto updatedOrder = orderService.updateOrderStatus(orderId, newStatus);
        return ResponseEntity.ok(updatedOrder);
    }

    // 주문 항목 수량 변경
    @PutMapping("/{customerId}/items/{orderItemId}")
    public ResponseEntity<List<OrderDto>> updateOrderItemQuantity(@PathVariable Long customerId, @PathVariable Long orderItemId, @RequestBody OrderItemDto orderItemDto) {
        List<OrderDto> updatedOrders = orderService.updateOrderItemQuantity(customerId, orderItemId, orderItemDto);
        return ResponseEntity.ok(updatedOrders);
    }

    // 주문 항목 삭제
    @DeleteMapping("/{customerId}/items/{orderItemId}")
    public ResponseEntity<List<OrderDto>> deleteOrderItem(@PathVariable Long orderItemId, @PathVariable Long customerId) {
        List<OrderDto> orderDto = orderService.deleteOrderItem(customerId, orderItemId);
        return ResponseEntity.ok(orderDto);
    }

    // 주문 삭제
    @DeleteMapping("/{customerId}/{orderId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<OrderDto>> deleteOrder(@PathVariable Long orderId, @PathVariable Long customerId) {
        List<OrderDto> orderDto = orderService.deleteOrder(orderId, customerId);
        return ResponseEntity.ok(orderDto);
    }
}
