package com.example.market.product.entity;

import lombok.*;

import javax.persistence.*;

/*
 * orderable 과 주문 가능 여부도 추가 하는 것은 어떨까 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "products")
public class Product {

    //상품 식별 id
    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //상품 이름
    @Column(name = "product_name", unique = true, nullable = false)
    private String productName;

    //상품 설명
    @Column(name = "product_description")
    private String productDescription;

    //상품 가격
    @Column(name = "product_price", nullable = false)
    private long price; //정수 가격만 사용하면 long 사용

    //상품 재고
    @Column(name = "stock", nullable = false)
    private int stock;


    // todo: 상품은 주문, 장바구니와의 연결이 필요. 이후 개발과정에서 추가
}
