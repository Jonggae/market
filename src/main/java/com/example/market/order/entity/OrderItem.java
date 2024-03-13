package com.example.market.order.entity;

import com.example.market.product.entity.Product;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    //상품과의 관계
    @ManyToOne
    private Product product;

    // 주문할 수량
    @Column(nullable = false)
    private int quantity;

    public Long getTotalPrice() {
        return product.getPrice() * quantity;
    }

    public void restockProduct() {
        this.product.increaseStock(this.quantity);
    }
}
