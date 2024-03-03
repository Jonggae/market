package com.example.market.order.entity;

import com.example.market.customer.entity.Customer;
import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @Column(name = "order_date",nullable = false)
    private LocalDateTime orderDate; // 주문 시간

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문 상태 "주문 완료", "배송 전 등"

    @ManyToOne
    private Customer customer; //한 유저는 여러개의 주문을 가질 수 있음

    // 주문할 상품들과의 관계
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);

    }
    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
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
