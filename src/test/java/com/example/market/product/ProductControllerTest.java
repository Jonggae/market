package com.example.market.product;

import com.example.market.product.controller.ProductController;
import com.example.market.product.dto.ProductDto;
import com.example.market.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@WithMockUser(roles = "ADMIN")
public class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;
    @Autowired
    private ObjectMapper objectMapper;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        productDto = new ProductDto(1L, "테스트 상품", "테스트 상품 정보", 1000L, 10L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("상품 등록 테스트")
    void addProductTest() throws Exception {
        when(productService.addProduct(any(ProductDto.class))).thenReturn(productDto);

        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName").value("테스트 상품"));
    }

    @Test
    @DisplayName("전체 상품목록 조회 테스트")
    void getAllProductsTest() throws Exception {
        List<ProductDto> allProducts = Collections.singletonList(productDto);
        when(productService.showAllProducts()).thenReturn(allProducts);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productName").value("테스트 상품"));
    }

    @Test
    @DisplayName("상품 단일 조회 테스트")
    void getProductTest() throws Exception {
        when(productService.showProductInfo(productDto.getId())).thenReturn(productDto);

        mockMvc.perform(get("/api/products/{id}", productDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("테스트 상품"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("상품 업데이트 테스트")
    void updateProductTest() throws Exception {
        when(productService.updateProduct(any(Long.class), any(ProductDto.class))).thenReturn(productDto);

        mockMvc.perform(put("/api/products/{id}", productDto.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("테스트 상품"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("상품 삭제 테스트")
    void deleteProductTest() throws Exception {
        mockMvc.perform(delete("/api/products/{id}", productDto.getId())
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
