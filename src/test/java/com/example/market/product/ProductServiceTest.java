package com.example.market.product;

import com.example.market.product.dto.ProductDto;
import com.example.market.product.repository.ProductRepository;
import com.example.market.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("상품 관련 서비스 테스트")
@SpringBootTest
@AutoConfigureMockMvc
public class ProductServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private long productId1;
    private long productId2;

    @BeforeEach
    void setUp() {
        ProductDto savedProduct1 = productService.addProduct(new ProductDto(null, "테스트 상품", "테스트 상품 설명", 1000L, 10L));
        ProductDto savedProduct2 = productService.addProduct(new ProductDto(null, "테스트 상품2", "테스트 상품 설명2", 2000L, 20L));

        productId1 = savedProduct1.getId();
        productId2 = savedProduct1.getId();
    }

    @Test
    @Transactional
    @DisplayName("관리자(ADMIN) 상품 등록 테스트")
    @WithMockUser(roles = "ADMIN")
    void addProductTestWithADMIN() throws Exception {
        ProductDto newProduct = new ProductDto(null, "New Product", "New Description", 2000L, 20L);
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(newProduct.getProductName() + " 해당 상품 등록이 완료되었습니다."));
    } @Test
    @Transactional
    @DisplayName("일반 사용자(USER) 상품 등록 테스트")
    @WithMockUser(roles = "USER")
    void addProductTestWithCustomer() throws Exception {
        ProductDto newProduct = new ProductDto(null, "New Product", "New Description", 2000L, 20L);
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @DisplayName("상품 전체 조회 서비스 테스트")
    void showAllProductTest() throws Exception {
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productName").value("테스트 상품"))
                .andExpect(jsonPath("$[1].productName").value("테스트 상품2"));
    }

    @Test
    @Transactional
    @DisplayName("상품 단일 조회 테스트")
    void ShowOneProductTest() throws Exception {
        // 상품 ID는 실제 환경에서 조회하여 설정

        mockMvc.perform(get("/api/products/" + productId1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("테스트 상품"));
    }

    @Test
    @DisplayName("상품 단일 조회 실패 테스트")
    @Transactional
    void failShowOneProductTEst() throws Exception{
        long nonExistProductId = 333;

        mockMvc.perform(get("/api/products/" + nonExistProductId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("상품이 존재하지 않습니다."));
    }

    @Test
    @Transactional
    @DisplayName("상품 정보 업데이트 테스트")
    @WithMockUser(roles = "ADMIN")
    void updateProductInfoTest() throws Exception {

        ProductDto updateProduct = new ProductDto(productId1, "업데이트된 상품", "업데이트된 상품 정보",1500L, 15L);
        mockMvc.perform(put("/api/products/"+productId1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(updateProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("업데이트된 상품"))
                .andExpect(jsonPath("$.productDescription").value("업데이트된 상품 정보"))
                .andExpect(jsonPath("$.price").value(1500))
                .andExpect(jsonPath("$.stock").value(15));
    }
}
