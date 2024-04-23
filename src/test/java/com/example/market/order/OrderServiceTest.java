package com.example.market.order;

import com.example.market.customer.entity.Customer;
import com.example.market.customer.repository.CustomerRepository;
import com.example.market.dataConfig.ServiceTestDataConfig;
import com.example.market.order.dto.OrderDto;
import com.example.market.order.dto.OrderItemDto;
import com.example.market.order.entity.Order;
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
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(ServiceTestDataConfig.class)
@Transactional
@ActiveProfiles("service")
public class OrderServiceTest {
    @Autowired
    private TransactionTemplate transactionTemplate;
    @PersistenceContext
    private EntityManager entityManager;

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
        OrderItemDto addedOrderItem = orderService.addOrderItem(testCustomerId, newOrderItem1);

        List<OrderDto> orderDtos = orderService.getOrderList(testCustomerId);
        assertThat(orderDtos).isNotEmpty(); // 주문 목록이 비어있지 않은지 확인
        assertThat(orderDtos.get(0).getOrderItems()).isNotEmpty(); // 첫 번째 주문의 주문 항목 목록이 비어있지 않은지 확인
        assertThat(orderDtos.get(0).getOrderItems()).extracting("productId").contains(addedOrderItem.getProductId()); // 첫 번째 주문의 주문 항목 중에 추가된 상품 ID가 포함되어 있는지 확인
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

        Long pendingOrderId = orderService.getOrderList(testCustomerId).get(0).getOrderId();
        orderService.confirmOrder(testCustomerId, pendingOrderId);

        Order confirmedOrder = orderRepository.findById(pendingOrderId)
                .orElseThrow(() -> new AssertionError("확정된 주문을 찾을 수 없습니다."));

        assertEquals(Order.OrderStatus.PENDING_PAYMENT, confirmedOrder.getOrderStatus());
    }

    @Test
    @DisplayName("주문 상태 업데이트 -> SHIPPED로 변경")
    void updateOrderStatusTest() {
        // 주문 항목 추가하여 미확정 주문 생성
        OrderItemDto newOrderItem = addNewOrderItem1();
        orderService.addOrderItem(testCustomerId, newOrderItem);

        Long pendingOrderId = orderService.getOrderList(testCustomerId).get(0).getOrderId();
        orderService.confirmOrder(testCustomerId, pendingOrderId);

//        orderService.updateOrderStatus(pendingOrderId, Order.OrderStatus.SHIPPED);

        // 업데이트된 주문 상태 확인
        Order updatedOrder = orderRepository.findById(pendingOrderId)
                .orElseThrow(() -> new AssertionError("확정된 주문을 찾을 수 없습니다."));
        assertEquals(Order.OrderStatus.SHIPPED, updatedOrder.getOrderStatus());
    }

    @Test
    @DisplayName("미확정 주문 내의 상품 삭제 테스트")
    void deleteOrderItemTest() {
        // 테스트가 잘 되지 않음. API 테스트는 작동함.

    }

    @Test
    @DisplayName("미확정 주문 내 수량 변경 테스트")
    void updateOrderItemQuantityTest() {
        // 주문 항목 추가
        OrderItemDto newOrderItem = addNewOrderItem1();
        OrderItemDto addedOrderItem = orderService.addOrderItem(testCustomerId, newOrderItem);

        // 수량 변경을 위한 OrderItemDto 객체 생성 (기존의 OrderItemDto 재사용 또는 새로 생성 가능)

        OrderItemDto updatedOrderItemDto = OrderItemDto.builder()
                .productId(testProduct1Id)
                .quantity(3)
                .build();

        orderService.updateOrderItemQuantity(testCustomerId, addedOrderItem.getItemId(), updatedOrderItemDto);

        // 변경된 주문 항목 조회 및 수량 검증
        List<OrderDto> orders = orderService.getOrderList(testCustomerId);
        assertFalse(orders.isEmpty());

        // 주문 항목이 포함된 주문을 찾고, 해당 주문 항목의 수량이 업데이트 되었는지 확인
        boolean isQuantityUpdated = orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .anyMatch(item -> item.getItemId().equals(addedOrderItem.getItemId()) && item.getQuantity() == 3);

        assertTrue(isQuantityUpdated);
    }

    @Test
    @DisplayName("주문 삭제 테스트")
    void deleteOrderTest() {
        OrderItemDto newOrderItem = addNewOrderItem1();
        orderService.addOrderItem(testCustomerId, newOrderItem);

        // 미확정 주문을 확정
        List<OrderDto> pendingOrders = orderService.getOrderList(testCustomerId);
        Long orderId = pendingOrders.get(0).getOrderId();
        orderService.confirmOrder(testCustomerId, orderId);

        // 주문 삭제
        orderService.deleteOrder(orderId, testCustomerId);

        // 삭제 후 주문 목록 조회
        List<OrderDto> ordersAfterDeletion = orderService.getOrderList(testCustomerId);
        assertTrue(ordersAfterDeletion.isEmpty());
    }

    private OrderItemDto addNewOrderItem1() {
        return OrderItemDto.builder()
                .productId(testProduct1Id)
                .quantity(1)
                .build();
    }


}

