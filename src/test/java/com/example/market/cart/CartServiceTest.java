package com.example.market.cart;

import com.example.market.cart.dto.CartDto;
import com.example.market.cart.dto.CartItemDto;
import com.example.market.cart.entity.Cart;
import com.example.market.cart.service.CartService;
import com.example.market.config.TestDataInitializerConfig;
import com.example.market.customer.entity.Customer;
import com.example.market.customer.repository.CustomerRepository;
import com.example.market.product.entity.Product;
import com.example.market.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestDataInitializerConfig.class)
@Transactional
@ActiveProfiles("test")
public class CartServiceTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CustomerRepository customerRepository;


    private Long testCustomerId;
    private Long testProductId;

    private Long cartId;

    @BeforeEach
    void setUp() {
        // 회원 정보는 initializer 사용
        // 테스트 상품 생성
        Product testProduct = Product.builder()
                .productName("테스트 상품")
                .productDescription("테스트 상품의 설명")
                .price(1000L)
                .stock(10L)
                .build();
        testProduct = productRepository.save(testProduct);
        testProductId = testProduct.getId();

        Customer testCustomer = customerRepository.findByCustomerName("testUser").orElseThrow();
        testCustomerId = testCustomer.getId();
    }

    @Test
    @DisplayName("장바구니 항목 추가 테스트")
    void addCartItemTest() {
        CartItemDto newCartItem = createNewCartItem();

        cartService.addCartItem(testCustomerId, newCartItem);

        CartDto cartDto = cartService.getCartItems(testCustomerId);
        assertThat(cartDto.getCartItems()).hasSizeGreaterThan(0);
    }

    @Test
    @DisplayName("장바구니 항목 수정 테스트 - 수량")
    void updateCartItemTest() {
        CartItemDto addedCartItem = cartService.addCartItem(testCustomerId, createNewCartItem());
        Long itemIdToUpdate = addedCartItem.getItemId(); // 실제 저장된 항목의 ID

        CartItemDto updatedCartItem = CartItemDto.builder()
                .productId(testProductId)
                .quantity(2) //수량 수정
                .build();

        cartService.updateCartItem(testCustomerId, itemIdToUpdate, updatedCartItem);

        CartDto cartDto = cartService.getCartItems(testCustomerId);
        assertThat(cartDto.getCartItems().stream()
                .anyMatch(item -> item.getQuantity() == 2)).isTrue();
    }

    @Test
    @DisplayName("장바구니 항목 삭제 테스트")
    void deleteCartItemTest() {
                // 먼저 항목을 추가
        CartItemDto addedCartItem = cartService.addCartItem(testCustomerId, createNewCartItem());
        Long itemIdToDelete = addedCartItem.getItemId(); // 실제 저장된 항목의 ID


        // 삭제할 상품의 id
        cartService.deleteCartItem(testCustomerId,testCustomerId, itemIdToDelete);

        CartDto cartDto = cartService.getCartItems(testCustomerId);
        assertThat(cartDto.getCartItems()).isEmpty();
    }

//    @Test
//    @DisplayName("장바구니 전체 비우기 테스트")
//    void clearCartTest() {
//        // 먼저 항목을 여러 개 추가
//        cartService.addCartItem(testCustomerId, new CartItemDto(testProductId, 1));
//        cartService.addCartItem(testCustomerId, new CartItemDto(testProductId, 2));
//
//        cartService.clearCart(testCustomerId);
//
//        CartDto cartDto = cartService.getCartItems(testCustomerId);
//        assertThat(cartDto.getCartItems()).isEmpty();
//    }

    private CartItemDto createNewCartItem() {
        return CartItemDto.builder()
                .productId(testProductId)
                .quantity(1)
                .build();
    }
}
