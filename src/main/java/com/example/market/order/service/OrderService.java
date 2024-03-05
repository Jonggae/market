package com.example.market.order.service;

import com.example.market.customer.entity.Customer;
import com.example.market.customer.repository.CustomerRepository;
import com.example.market.order.dto.OrderDto;
import com.example.market.order.dto.OrderItemDto;
import com.example.market.order.entity.Order;
import com.example.market.order.entity.Order.OrderStatus;
import com.example.market.order.entity.OrderItem;
import com.example.market.order.repository.OrderItemRepository;
import com.example.market.order.repository.OrderRepository;
import com.example.market.product.entity.Product;
import com.example.market.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    // 확정 주문은 미확정 주문들의 목록에서 [주문하기] 버튼을 눌러 확정된 상태이며, 결제 유무는 고려하지 않았음.(결제 이전으로 설정)

    // 미확정 주문 생성
    public Order createPendingOrder(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));

        Order newOrder = Order.builder()
                .customer(customer)
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.PENDING_ORDER)
                .build();
        return orderRepository.save(newOrder);
    }

    // 전체 주문 조회(확정, 미확정 전부)
    public List<OrderDto> getOrderList(Long customerId) {
        List<Order> orders = orderRepository.findAllByCustomerId(customerId);

        return orders.stream().map(order -> {
            List<OrderItemDto> orderItems = order.getOrderItems().stream()
                    .map(OrderItemDto::from)
                    .collect(Collectors.toList());

            return OrderDto.builder()
                    .orderId(order.getId())
                    .customerId(customerId)
                    .orderItems(orderItems)
                    .status(order.getOrderStatus()) // 실제 주문의 상태를 반영
                    .orderDateTime(order.getOrderDate())
                    .build();
        }).collect(Collectors.toList());
    }

    // 미확정 주문에 상품 추가하기 (아직 주문하기를 누르기 전임)
    public OrderItemDto addOrderItem(Long customerId, OrderItemDto orderItemDto) {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("해당 유저의 정보를 찾을 수 없습니다."));
        Optional<Order> existingOrder = orderRepository.findByCustomerIdAndOrderStatus(customerId, OrderStatus.PENDING_ORDER);
        Order order = existingOrder.orElseGet(() -> createPendingOrder(customerId));

        Product product = productRepository.findById(orderItemDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다"));

        OrderItem newOrderItem = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(orderItemDto.getQuantity())
                .build();

        order.getOrderItems().add(newOrderItem);
        orderRepository.save(order);

        return OrderItemDto.from(newOrderItem);
    }

    // 주문을 확정함
    public OrderDto confirmOrder(Long customerId, Long orderId) {
        Order extstingOrder = orderRepository.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> new RuntimeException("미확정 주문이 없습니다."));

        extstingOrder.setOrderStatus(OrderStatus.PENDING_PAYMENT); // 주문 확정이므로 상태 변경함
        extstingOrder.setOrderDate(LocalDateTime.now());

        return OrderDto.from(orderRepository.save(extstingOrder));
    }

    // 결제, 배송 등 이후 주문 상태 업데이트
    public OrderDto updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));
        order.setOrderStatus(newStatus);
        return OrderDto.from(orderRepository.save(order));
    }

    // 미확정 주문 내 상품 수량 변경하기
    public List<OrderDto> updateOrderItemQuantity(Long customerId, Long orderItemId, OrderItemDto orderItemDto) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 없습니다."));
        orderItem.setQuantity(orderItemDto.getQuantity());
        orderItemRepository.save(orderItem);
        return getOrderList(customerId);

    }

    //미확정 주문 내 상품 삭제하기
    public List<OrderDto> deleteOrderItem(Long customerId, Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("주문 항목을 찾을 수 없습니다."));

        orderItemRepository.delete(orderItem);
        return getOrderList(customerId);
    }

    // 주문 삭제하기
    public List<OrderDto> deleteOrder(Long orderId, Long customerId) {
        Order order = orderRepository.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));
        orderItemRepository.deleteAllByOrder(order);

        orderRepository.delete(order);
        return getOrderList(customerId);
    }
}