package com.example.market.cart.entity;

import com.example.market.customer.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

// Cart (장바구니) 엔티티
/*
* 회원 가입 시 하나의 장바구니가 함께 생성되어야 함.
* 상품을 담을 수 있어야 함
* 상품 재고가 없으면 담을 수 없음.*/
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name = "cart")
public class Cart {

    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Cart(Long customerId) {
    }
}
