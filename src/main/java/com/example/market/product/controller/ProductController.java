package com.example.market.product.controller;

import com.example.market.commons.MessageUtil;
import com.example.market.commons.apiResponse.ApiResponseDto;
import com.example.market.commons.apiResponse.ApiResponseUtil;
import com.example.market.product.message.ProductApiMessage;
import com.example.market.product.dto.ProductDto;
import com.example.market.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    // 상품등록
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponseDto<ProductDto>> addProduct(@RequestBody ProductDto productDto) {
        ProductDto addedProduct = productService.addProduct(productDto);
        String message = MessageUtil.getFormattedMessage(ProductApiMessage.PRODUCT_ADD_SUCCESS, productDto.getProductName());
        return ApiResponseUtil.success(message, addedProduct, 200);
    }

    // 전체 상품목록 조회
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<ProductDto>>> getProductList() {
        List<ProductDto> products = productService.showAllProducts();
        String message =MessageUtil.getMessage(ProductApiMessage.PRODUCT_LIST_SUCCESS);
        return ApiResponseUtil.success(message, products, 200);
    }

    // 상품 단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<ProductDto>> getProduct(@PathVariable Long id) {
        ProductDto product = productService.showProductInfo(id);
        String message = MessageUtil.getFormattedMessage(ProductApiMessage.PRODUCT_DETAIL_SUCCESS, product.getProductName());
        return ApiResponseUtil.success(message, product, 200);
    }

    // 상품 업데이트
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponseDto<ProductDto>> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        ProductDto updatedProduct = productService.updateProduct(id, productDto);
        String message = MessageUtil.getMessage(ProductApiMessage.PRODUCT_UPDATE_SUCCESS);
        return ApiResponseUtil.success(message, updatedProduct, 200);
    }

    // 상품 삭제
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<List<ProductDto>>> deleteProduct(@PathVariable Long id) {
        List<ProductDto> afterDeletionProducts = productService.deleteProduct(id);
        String message = MessageUtil.getMessage(ProductApiMessage.PRODUCT_DELETE_SUCCESS);
        return ApiResponseUtil.success(message, afterDeletionProducts, 200);
    }
}
