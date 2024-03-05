package com.example.market.cart.entity;

import com.example.market.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name = "cart_item")
public class CartItem {

    @Id
    @Column(name = "cart_item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // 장바구니 내의 갯수 + 상품가격을 이용한 합계 가격
    public Long getTotalPrice() {
        return product.getPrice() * quantity;
    }
    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", productName=" + product.getProductName() +
                ", quantity=" + quantity +
                ", price=" + product.getPrice() +
                '}';
    }
}
