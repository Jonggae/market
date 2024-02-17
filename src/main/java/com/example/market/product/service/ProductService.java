package com.example.market.product.service;

import com.example.market.product.dto.ProductDto;
import com.example.market.product.entity.Product;
import com.example.market.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/*
 * 상품 서비스에 필요한 메서드들이 무엇이 있을까?
 * 1. 상품 전체 목록 조회 -> 전체 목록 조회 시 상품 이름, 상품 설명, 가격, 재고 수량을 전부 표시하는것이 좋은가?
 * 2. 상품 1개 조회 -> 이때에는 상품 이름, 설명, 가격, 재고 수량을 전부 표시
 * 3. 이후 주문(order) 장바구니(cart)와 연결할 방법이 필요함*/
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    //상품들 전체 조회
    public List<ProductDto> showAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(ProductDto::from)
                .collect(Collectors.toList());
    }

    public ProductDto showProductInfo(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            return ProductDto.from(product);
        } else {
            return null;
        }

    }
}
