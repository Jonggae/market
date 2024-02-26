package com.example.market.product.controller;

import com.example.market.etc.apiResponse.ApiResponseDto;
import com.example.market.product.dto.ProductDto;
import com.example.market.product.service.ProductService;
import com.example.market.security.exception.NotFoundProductException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    // 상품등록
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponseDto> addProduct(@RequestBody ProductDto productDto) {
        productService.addProduct(productDto);
        ApiResponseDto apiResponseDto = new ApiResponseDto(productDto.getProductName() + " 해당 상품 등록이 완료되었습니다.");
        return ResponseEntity.ok(apiResponseDto);
    }

    // 전체 상품목록 조회
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> products = productService.showAllProducts();
        return ResponseEntity.ok(products);
    }

    // 상품 단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        try {
            ProductDto product = productService.showProductInfo(id);
            return ResponseEntity.ok(product);
        } catch (NotFoundProductException e) {
            return ResponseEntity.ok(new ApiResponseDto("상품이 존재하지 않습니다."));
        }
    }

    // 상품 업데이트
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        ProductDto updatedProduct = productService.updateProduct(id, productDto);
        return ResponseEntity.ok(updatedProduct);
    }

    // 상품 삭제
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
