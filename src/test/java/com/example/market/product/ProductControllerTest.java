package com.example.market.product;

import com.example.market.exception.NotFoundProductException;
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
@DisplayName("상품 관련 컨트롤러 테스트")
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(productDto.getProductName() + " : 해당 상품 등록이 완료되었습니다."));
    }

    @Test
    @DisplayName("전체 상품목록 조회 테스트")
    void getAllProductsTest() throws Exception {
        List<ProductDto> allProducts = Collections.singletonList(productDto);
        when(productService.showAllProducts()).thenReturn(allProducts);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("전체 상품 리스트입니다."))
                .andExpect(jsonPath("$.data[0].productName").value("테스트 상품"))
                .andExpect(jsonPath("$.data[0].productDescription").value("테스트 상품 정보"))
                .andExpect(jsonPath("$.data[0].price").value(1000L))
                .andExpect(jsonPath("$.data[0].stock").value(10L));
    }

    @Test
    @DisplayName("상품 단일 조회 테스트")
    void getProductTest() throws Exception {
        when(productService.showProductInfo(productDto.getId())).thenReturn(productDto);

        mockMvc.perform(get("/api/products/{id}", productDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(productDto.getProductName() + "의 상품 정보입니다"));
    }

    @Test
    @DisplayName("상품 단일 조회 실패 테스트")
    void failGetProductTest() throws Exception {
        Long nonExistProductId = 4L;
        when(productService.showProductInfo(nonExistProductId)).thenThrow(NotFoundProductException.class);

        mockMvc.perform(get("/api/products/{id}", nonExistProductId)) // 없는 번호 조회
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("해당 상품이 존재하지 않습니다."));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("상품 정보 업데이트 테스트")
    void updateProductTest() throws Exception {

        when(productService.updateProduct(any(Long.class), any(ProductDto.class))).thenReturn(productDto);

        mockMvc.perform(put("/api/products/{id}", productDto.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productName").value("테스트 상품"))
                .andExpect(jsonPath("$.message").value("상품 정보가 수정 되었습니다."));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("상품 삭제 테스트")
    void deleteProductTest() throws Exception {
        mockMvc.perform(delete("/api/products/{id}", productDto.getId())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("상품이 삭제 되었습니다."));
    }
}
