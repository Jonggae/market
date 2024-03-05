package com.example.market.product.controller;

import com.example.market.commons.apiResponse.ApiResponseDto;
import com.example.market.exception.NotFoundProductException;
import com.example.market.product.dto.ProductDto;
import com.example.market.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    // 상품등록
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponseDto<ProductDto>> addProduct(@RequestBody ProductDto productDto) {
        ProductDto updatedProductDto = productService.addProduct(productDto);
        ApiResponseDto<ProductDto> response = new ApiResponseDto<>(productDto.getProductName() + " : 해당 상품 등록이 완료되었습니다.", updatedProductDto);
        return ResponseEntity.ok(response);
    }

    // 전체 상품목록 조회
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<ProductDto>>> getAllProducts() {
        List<ProductDto> products = productService.showAllProducts();
        ApiResponseDto<List<ProductDto>> response = new ApiResponseDto<>("전체 상품 리스트입니다", products);
        return ResponseEntity.ok(response);
    }

    // 상품 단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<ProductDto>> getProduct(@PathVariable Long id) {
        try {
            ProductDto product = productService.showProductInfo(id);
            ApiResponseDto<ProductDto> response = new ApiResponseDto<>(product.getProductName() + "의 상품 정보입니다", product);
            return ResponseEntity.ok(response);
        } catch (NotFoundProductException e) {
            return ResponseEntity.ok(new ApiResponseDto<>("해당 상품이 존재하지 않습니다."));
        }
    }

    // 상품 업데이트
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponseDto<ProductDto>> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        ProductDto updatedProduct = productService.updateProduct(id, productDto);
        ApiResponseDto<ProductDto> response = new ApiResponseDto<>("상품 정보가 수정 되었습니다", updatedProduct);
        return ResponseEntity.ok(response);
    }

    // 상품 삭제
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<List<ProductDto>>> deleteProduct(@PathVariable Long id) {
        List<ProductDto> updatedProducts = productService.deleteProduct(id);

        String message = "상품이 삭제 되었습니다";
        ApiResponseDto<List<ProductDto>> response = new ApiResponseDto<>(message, updatedProducts);
        return ResponseEntity.ok(response);
    }
}
