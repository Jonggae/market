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
    /*
     * 리팩토링 방향
     * 1. 중복 코드 제거 - getOrderList , updateOrderStatus
     * 2. 메서드 분리 - confirmOrder, updateOrderItemQuantity
     * 3. 예외 처리 개선 - 사용자 정의 예외로 사용
     * 4. 불필요한 코드 제거 -  createPendingOrder
     * 5. 코드가독성 개선 - 전체
     * 6. 트랜잭션 관리 개선 - 다른 메서드에서도 @Transaction이 필요할 수도
     * 7. 도메인 모델 개선 - entity와 Dto가 혼재되어있음.
     * 8. 의존성 주입 개선 - 생성자 주입 vs 필드 주입 일관성있게 설정 */


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

        // 재고 줄이고 확인
        boolean stockReduced = product.reduceStock(orderItemDto.getQuantity());
        if (!stockReduced) {
            throw new InsufficientStockException(product.getProductName());
        }

        OrderItem newOrderItem = order.addOrderItem(product, orderItemDto.getQuantity());
        orderRepository.save(order);

        return OrderItemDto.from(newOrderItem);
    }

    // 주문을 확정함
    @Transactional
    public OrderDto confirmOrder(Long customerId, Long orderId) {
        Order extstingOrder = orderRepository.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(NotFoundOrderException::new);
        validateAndReduceStock(extstingOrder); //재고 확인
        extstingOrder.confirmOrder(); // 주문 상태 변경

        return OrderDto.from(orderRepository.save(extstingOrder));
    }

    // 재고 확인 로직
    private void validateAndReduceStock(Order order) {
        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            if (!product.reduceStock(orderItem.getQuantity())) {
                throw new InsufficientStockException("상품 수량이 충분하지 않습니다.");
            }
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

    // 주문 삭제하기
    @Transactional
    public List<OrderDto> deleteOrder(Long orderId, Long customerId) {
        Order order = orderRepository.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(NotFoundOrderException::new);
        orderItemRepository.deleteAllByOrder(order);

        orderRepository.delete(order);
        return getOrderList(customerId);
    }
}
