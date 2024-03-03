package com.example.market.order;

import com.example.market.customer.entity.Customer;
import com.example.market.customer.repository.CustomerRepository;
import com.example.market.dataConfig.ServiceTestDataConfig;
import com.example.market.order.dto.OrderDto;
import com.example.market.order.dto.OrderItemDto;
import com.example.market.order.entity.Order;
import com.example.market.order.entity.OrderItem;
import com.example.market.order.repository.OrderItemRepository;
import com.example.market.order.repository.OrderRepository;
import com.example.market.order.service.OrderService;
import com.example.market.product.entity.Product;
import com.example.market.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(ServiceTestDataConfig.class)
@Transactional
@ActiveProfiles("service")
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private Long testCustomerId;
    private Long testProduct1Id;
    private Long testProduct2Id;

    @BeforeEach
    void setUp() {
        // initializer + 테스트 상품 생성

        Product testProduct1 = productRepository.findByProductName("테스트 상품 1").orElseThrow(() -> new EntityNotFoundException("해당 상품이 없습니다."));
        testProduct1Id = testProduct1.getId();
        Product testProduct2 = productRepository.findByProductName("테스트 상품 2").orElseThrow();
        testProduct2Id = testProduct2.getId();
        Customer testCustomer = customerRepository.findByCustomerName("testUser").orElseThrow();
        testCustomerId = testCustomer.getId();

    }

    @Test
    @DisplayName("내 주문 조회 테스트, 미확정 1개 존재")
    void getOrders() {
        addNewOrderItem1(); // 미확정 주문에 들어있는 상품이 없어서 오류가났으므로 추가해줌.

        List<OrderDto> orderDtos = orderService.getOrderList(testCustomerId);
        assertFalse(orderDtos.isEmpty()); // isEmpty가 아닌 경우를 검증
        assertEquals(1, orderDtos.size()); // 미확정 주문이 정확히 1개 존재하는지 검증
        assertEquals(Order.OrderStatus.PENDING_ORDER, orderDtos.get(0).getStatus()); // 첫 번째 주문의 상태가 PENDING_ORDER인지 검증
    }

    @Test
    @DisplayName("주문 항목 추가 테스트")
    void addOrderItemTest() {
        OrderItemDto newOrderItem1 = addNewOrderItem1(); //@Transaction 이 걸려있으므로 1번을 넣어도 상관없음
        OrderItemDto addOrderItem = orderService.addOrderItem(testCustomerId, newOrderItem1);

        assertNotNull(addOrderItem);
        assertEquals(testProduct1Id, addOrderItem.getProductId());
        assertEquals(newOrderItem1.getQuantity(), addOrderItem.getQuantity());
        assertEquals(newOrderItem1.getPrice(), addOrderItem.getPrice());
    }

    @Test
    @DisplayName("미확정 주문 생성 테스트 - 바로 상품을 주문하였을 때 생성")
    void createPendingOrderTest() {
        Order createdOrder = orderService.createPendingOrder(testCustomerId);
        assertNotNull(createdOrder);
        assertEquals(Order.OrderStatus.PENDING_ORDER, createdOrder.getOrderStatus());
    }

    @Test
    @DisplayName("주문 확정 테스트 -> orderStatus 변경되는지 확인")
    void confirmOrderTest() {
        // 미확정 주문 생성 및 추가
        OrderItemDto newOrderItem = addNewOrderItem1();
        orderService.addOrderItem(testCustomerId, newOrderItem);

        // 생성된 미확정 주문 ID 가져오기
        Long pendingOrderId = orderService.getOrderList(testCustomerId).get(0).getOrderId();

        // 주문 확정
        orderService.confirmOrder(testCustomerId, pendingOrderId);

        // 주문 상태 확인
        OrderDto confirmedOrder = orderService.getOrderList(testCustomerId).stream()
                .filter(order -> order.getOrderId().equals(pendingOrderId))
                .findFirst()
                .orElseThrow(() -> new AssertionError("확정된 주문을 찾을 수 없습니다."));

        assertEquals(Order.OrderStatus.PENDING_PAYMENT, confirmedOrder.getStatus());

    }

    @Test
    @DisplayName("주문 상태 업데이트 -> SHIPPED로 변경")
    void updateOrderStatusTest() {
        // 주문 항목 추가하여 미확정 주문 생성
        OrderItemDto newOrderItem = addNewOrderItem1();
        orderService.addOrderItem(testCustomerId, newOrderItem);

        // 미확정 주문을 확정하여 주문 상태를 PENDING_PAYMENT로 변경
        List<OrderDto> pendingOrders = orderService.getOrderList(testCustomerId);
        assertFalse(pendingOrders.isEmpty(), "미확정 주문이 존재해야 합니다.");
        OrderDto pendingOrder = pendingOrders.stream()
                .filter(order -> order.getStatus() == Order.OrderStatus.PENDING_ORDER)
                .findFirst()
                .orElseThrow(() -> new AssertionError("미확정 주문을 찾을 수 없습니다."));
        Long orderId = pendingOrder.getOrderId();

        // 주문 확정
        OrderDto confirmedOrderDto = orderService.confirmOrder(testCustomerId, orderId);

        // 주문 상태를 SHIPPED로 업데이트
        OrderDto updatedOrderDto = orderService.updateOrderStatus(confirmedOrderDto.getOrderId(), Order.OrderStatus.SHIPPED);

        // 업데이트된 주문 상태 확인
        assertEquals(Order.OrderStatus.SHIPPED, updatedOrderDto.getStatus(), "주문 상태가 SHIPPED로 업데이트되어야 합니다.");

    }

    @Test
    @DisplayName("미확정 주문 내의 상품 삭제 테스트")
    void deleteOrderItemTest() {


    }

    @Test
    @DisplayName("미확정 주문 내 수량 변경 테스트")
    void updateOrderItemQuantityTest() {
        // 먼저, 새 주문 항목을 추가합니다.
        OrderItemDto newOrderItem = addNewOrderItem1();
        OrderItemDto addedOrderItem = orderService.addOrderItem(testCustomerId, newOrderItem);

        // 변경할 수량을 설정합니다.
        int updatedQuantity = 5;

        // 수량 변경을 위한 DTO를 생성합니다.
        OrderItemDto updateOrderItemDto = OrderItemDto.builder()
                .productId(addedOrderItem.getProductId())
                .quantity(updatedQuantity)
                .build();

        // 수량 변경을 실행합니다.
        List<OrderDto> updatedOrderList = orderService.updateOrderItemQuantity(testCustomerId, addedOrderItem.getItemId(), updateOrderItemDto);

        // 변경된 주문 항목을 검색하여, 수량이 업데이트되었는지 확인합니다.
        boolean quantityUpdated = false;
        for (OrderDto orderDto : updatedOrderList) {
            for (OrderItemDto itemDto : orderDto.getOrderItems()) {
                if (itemDto.getItemId().equals(addedOrderItem.getItemId()) && itemDto.getQuantity() == updatedQuantity) {
                    quantityUpdated = true;
                    break;
                }
            }
            if (quantityUpdated) break;
        }

        // 검증: 수량이 정확히 업데이트되었는지 확인합니다.
        assertTrue(quantityUpdated, "수량이 정확히 업데이트되어야 합니다.");
    }

    @Test
    @DisplayName("주문 삭제 테스트")
    void deleteOrderTest() {

    }

    private OrderItemDto addNewOrderItem1() {
        Product testProduct1 = productRepository.findByProductName("테스트 상품 1").orElseThrow();
        return OrderItemDto.builder()
                .productId(testProduct1Id)
                .productName(testProduct1.getProductName())
                .quantity(1)
                .price(testProduct1.getPrice())
                .build();
    }

    private OrderItemDto addNewOrderItem2() {
        Product testProduct1 = productRepository.findByProductName("테스트 상품 1").orElseThrow();
        return OrderItemDto.builder()
                .productId(testProduct1Id)
                .productName(testProduct1.getProductName())
                .quantity(1)
                .price(testProduct1.getPrice())
                .build();
    }

}

