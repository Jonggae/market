package com.example.market.order.service;

import com.example.market.customer.entity.Customer;
import com.example.market.customer.repository.CustomerRepository;
import com.example.market.exception.*;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
                .orElseThrow(NotFoundMemberException::new);

        Order newOrder = Order.builder()
                .customer(customer)
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.PENDING_ORDER)
                .build();
        return orderRepository.save(newOrder);
    }

    // 전체 주문 조회(확정, 미확정 전부)
    public List<OrderDto> getOrderList(Long customerId) {
        return orderRepository.findAllByCustomerId(customerId).stream()
                .map(OrderDto::from)
                .collect(Collectors.toList());
    }

    // 미확정 주문에 상품 추가하기 (아직 주문하기를 누르기 전임)
    public OrderItemDto addOrderItem(Long customerId, OrderItemDto orderItemDto) {
        customerRepository.findById(customerId)
                .orElseThrow(NotFoundMemberException::new);

        Order order = orderRepository.findByCustomerIdAndOrderStatus(customerId, OrderStatus.PENDING_ORDER)
                .orElseGet(() -> createPendingOrder(customerId));

        Product product = productRepository.findById(orderItemDto.getProductId())
                .orElseThrow(NotFoundProductException::new);

        // 재고 확인 -> 재고보다 더 많은 수량을 추가하려 했을때 예외 + 실제로 재고를 감소시키지 않음
        if (!product.checkStock(orderItemDto.getQuantity())) {
            throw new InsufficientStockException(orderItemDto.getProductName());
        }

        OrderItem newOrderItem = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(orderItemDto.getQuantity())
                .build();

        newOrderItem = orderItemRepository.save(newOrderItem);
        order.getOrderItems().add(newOrderItem);
        orderRepository.save(order);

        return OrderItemDto.from(newOrderItem);
    }

    // 주문을 확정함 - 재고가 실제로 감소되는 시점
    @Transactional
    public OrderDto confirmOrder(Long customerId, Long orderId) {
        Order extstingOrder = orderRepository.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(NotFoundOrderException::new);
        validateAndReduceStock(extstingOrder); //재고 확인, 재고 감소
        extstingOrder.confirmOrder(); // 주문 상태 변경

        return OrderDto.from(orderRepository.save(extstingOrder));
    }

    // 재고 확인, 상품이 여러개일 수 있음.
    private void validateAndReduceStock(Order order) {
        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            if (!product.checkStock(orderItem.getQuantity())) {
                throw new InsufficientStockException(product.getProductName());
            }
            product.decreaseStock(orderItem.getQuantity());
            productRepository.save(product);
        }
    }

    // 결제, 배송 등 이후 주문 상태 업데이트
    public OrderDto updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(NotFoundOrderException::new);

        order.updateOrderStatus(newStatus);
        return OrderDto.from(orderRepository.save(order));
    }

    // 미확정 주문 내 상품 수량 변경하기
    public List<OrderDto> updateOrderItemQuantity(Long customerId, Long orderItemId, OrderItemDto orderItemDto) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(NotFoundOrderItemException::new);

        Order order = orderItem.getOrder();
        order.validateOrderStatusForUpdate();

        long quantityDifference = orderItemDto.getQuantity() - orderItem.getQuantity();
        Product product = orderItem.getProduct();
        product.updateStock(-quantityDifference); //재고 업데이트

        orderItem.setQuantity(orderItemDto.getQuantity());
        orderItemRepository.save(orderItem);
        return getOrderList(customerId);
    }

    //미확정 주문 내 상품 삭제하기
    public List<OrderDto> deleteOrderItem(Long customerId, Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(NotFoundOrderItemException::new);

        Product product = orderItem.getProduct();
        product.setStock(product.getStock() + orderItem.getQuantity());
        productRepository.save(product);

        orderItemRepository.delete(orderItem);
        return getOrderList(customerId);
    }

    // 주문 삭제하기 -> 주문 객체 자체를 삭제함
    @Transactional
    public List<OrderDto> deleteOrder(Long orderId, Long customerId) {
        Order order = orderRepository.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(NotFoundOrderException::new);
        orderItemRepository.deleteAllByOrder(order);

        orderRepository.delete(order);
        return getOrderList(customerId);
    }
}
