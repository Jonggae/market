package com.example.market.product.entity;

import com.example.market.exception.InsufficientStockException;
import com.example.market.product.dto.ProductDto;
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
    private Long stock;

    public void updateFromDto(ProductDto productDto) {
        this.productName = productDto.getProductName();
        this.productDescription = productDto.getProductDescription();
        this.price = productDto.getPrice();
        this.stock = productDto.getStock();
    }

    // 재고 하락 -> 재고가 충분할때만
    public void decreaseStock(int quantity) {
        this.stock -= quantity;
    }

    public boolean checkStock(int quantity) {
        return this.stock >= quantity;
    }

    public void increaseStock(int quantity) {
        this.stock += quantity;
    }

    public void updateStock(long quantityDifference) {
        long updatedStock = this.stock - quantityDifference;
        if (updatedStock < 0) {
            throw new InsufficientStockException(this.productName);
        }
        this.stock = updatedStock;
    }


}
