package com.example.market.product;

import com.example.market.product.dto.ProductDto;
import com.example.market.product.repository.ProductRepository;
import com.example.market.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


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
        ProductDto savedProduct1 = productService
                .addProduct(new ProductDto(null, "테스트 상품", "테스트 상품 설명", 1000L, 10L));

        ProductDto savedProduct2 = productService
                .addProduct(new ProductDto(null, "테스트 상품2", "테스트 상품 설명2", 2000L, 20L));

        productId1 = savedProduct1.getId();
        productId2 = savedProduct2.getId();
    }

    @Test
    @Transactional
    @DisplayName("관리자(ADMIN) 상품 등록 테스트")
    @WithMockUser(roles = "ADMIN")
    void addProductTestWithADMIN() throws Exception {
        ProductDto newProduct = addNewProduct();
        ProductDto savedProduct = productService.addProduct(newProduct);

        assertNotNull(savedProduct.getId());
        assertEquals("테스트으 상품", savedProduct.getProductName());
        assertEquals("테스트으 상품 설명", savedProduct.getProductDescription());
        assertEquals(5000L, savedProduct.getPrice());
    }

    @Test
    @Transactional
    @DisplayName("상품 전체 조회 서비스 테스트")
    void showAllProductTest() throws Exception {

        List<ProductDto> products = productService.showAllProducts();
        assertTrue(products.size() >= 2);
    }

    @Test
    @Transactional
    @DisplayName("상품 단일 조회 테스트")
    void ShowOneProductTest() throws Exception {
        ProductDto product = productService.showProductInfo(productId1);

        // 검증: 상품 정보가 정확히 조회되는지 검증
        assertNotNull(product);
        assertEquals("테스트 상품", product.getProductName());
    }

    @Test
    @Transactional
    @DisplayName("상품 정보 업데이트 테스트")
    @WithMockUser(roles = "ADMIN")
    void updateProductInfoTest() throws Exception {
        ProductDto updatedProduct = productService
                .updateProduct(productId1, new ProductDto(productId1, "업데이트된 상품", "업데이트된설명", 20000L, 200L));

        assertEquals("업데이트된 상품", updatedProduct.getProductName());
        assertEquals(20000L, updatedProduct.getPrice());
    }

    private ProductDto addNewProduct() {
        return ProductDto.builder()
                .productName("테스트으 상품")
                .productDescription("테스트으 상품 설명")
                .price(5000L)
                .stock(1000L)
                .build();
    }
}
