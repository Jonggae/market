package com.example.market.order.entity;

import com.example.market.customer.entity.Customer;
import com.example.market.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "orders")
public class Order {
    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate; // 주문 시간

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문 상태 "주문 완료", "배송 전 등"

    @ManyToOne
    private Customer customer; //한 유저는 여러개의 주문을 가질 수 있음

    // 주문할 상품들과의 관계
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    // 주문 확정용으로만 사용
    public void confirmOrder() {
        this.orderStatus = OrderStatus.PENDING_PAYMENT;
        this.orderDate = LocalDateTime.now();
    }

    // 주문의 상태 변경(업데이트)
    public void updateOrderStatus(OrderStatus newStatus) {
        if (newStatus == OrderStatus.CANCELLED || newStatus == OrderStatus.PENDING_ORDER) {
            this.orderItems.forEach(OrderItem::restockProduct);
        }
        this.orderStatus = newStatus;
    }

    public void validateOrderStatusForUpdate() {
        if (this.orderStatus != OrderStatus.PENDING_ORDER) {
            throw new RuntimeException("주문 상태가 대기중이 아니므로 주문 항목을 변경할 수 없습니다.");
        }
    }

    public enum OrderStatus {
        PENDING_ORDER, // 주문 대기
        PENDING_PAYMENT, //결제 대기
        PAID, // 결제 완료
        PREPARING_FOR_SHIPMENT, // 배송 준비 중
        SHIPPED, // 배송 중
        DELIVERED, // 배송 완료
        CANCELLED // 주문 취소
    }
}
