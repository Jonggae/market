package com.example.market.product.service;

import com.example.market.product.dto.ProductDto;
import com.example.market.product.entity.Product;
import com.example.market.product.repository.ProductRepository;
import com.example.market.security.exception.NotFoundProductException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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

    //상품 등록
    public ProductDto addProduct(ProductDto productDto) {
        Product product = Product.builder()
                .productName(productDto.getProductName())
                .productDescription(productDto.getProductDescription())
                .price(productDto.getPrice())
                .stock(productDto.getStock())
                .build();

        return ProductDto.from(productRepository.save(product));
    }

    //상품들 전체 조회
    public List<ProductDto> showAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(ProductDto::from)
                .collect(Collectors.toList());
    }

    // 상품 단일조회
    public ProductDto showProductInfo(Long productId) {
        return productRepository.findById(productId)
                .map(ProductDto::from)
                .orElseThrow(NotFoundProductException::new);

    }

    // 상품 업데이트
    public ProductDto updateProduct(Long productId, ProductDto productDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다" + productId));
        product.setProductName(productDto.getProductName());
        product.setProductDescription(productDto.getProductDescription());
        product.setPrice(productDto.getPrice());
        product.setStock(productDto.getStock());
        productRepository.save(product);
        return ProductDto.from(product);
    }

    // 상품 삭제
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다"));
        productRepository.delete(product);
    }
}
