package com.example.market.cart;

import com.example.market.cart.dto.CartDto;
import com.example.market.cart.dto.CartItemDto;
import com.example.market.cart.entity.Cart;
import com.example.market.cart.service.CartService;
import com.example.market.customer.entity.Customer;
import com.example.market.customer.repository.CustomerRepository;
import com.example.market.dataConfig.ServiceTestDataConfig;
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
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import(ServiceTestDataConfig.class)
@Transactional
@ActiveProfiles("service")
public class CartServiceTest {
    @Autowired
    private TransactionTemplate transactionTemplate;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CartService cartService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CustomerRepository customerRepository;


    private Long testCustomerId;
    private Long testProduct1Id;
    private Long testProduct2Id;

    @BeforeEach
    void setUp() {
        // 회원 정보는 initializer 사용
        // 테스트 상품 생성

        Product testProduct1 = productRepository.findByProductName("테스트 상품 1").orElseThrow();
        testProduct1Id = testProduct1.getId();
        Product testProduct2 = productRepository.findByProductName("테스트 상품 2").orElseThrow();
        testProduct2Id = testProduct2.getId();
        Customer testCustomer = customerRepository.findByCustomerName("testUser").orElseThrow();
        testCustomerId = testCustomer.getId();
    }

    @Test
    @DisplayName("장바구니 조회 테스트")
    void getCartItemsTest() {
        CartDto cartDto = cartService.getCartItems(testCustomerId);
        assertThat(cartDto).isNotNull();
        assertThat(cartDto.getCustomerId()).isEqualTo(testCustomerId);
    }

    @Test
    @DisplayName("장바구니 항목 추가 테스트")
    void addCartItemTest() {
        CartItemDto newCartItem = addNewCartItem1();

        cartService.addCartItem(testCustomerId, newCartItem);
        // product1만 추가 한 경우

        CartDto cartDto = cartService.getCartItems(testCustomerId);
        assertThat(cartDto.getCartItems()).hasSizeGreaterThan(0);
    }

    @Test
    @DisplayName("장바구니 항목 수정 테스트 - 수량")
    void updateCartItemTest() {
        CartItemDto addedCartItem = cartService.addCartItem(testCustomerId, addNewCartItem1());
        Long itemIdToUpdate = addedCartItem.getItemId(); // 저장된 항목의 ID

        CartItemDto updatedCartItem = CartItemDto.builder()
                .productId(testProduct1Id)
                .quantity(2) //수량 수정
                .build();

        cartService.updateCartItem(testCustomerId, itemIdToUpdate, updatedCartItem);

        CartDto cartDto = cartService.getCartItems(testCustomerId);
        assertThat(cartDto.getCartItems().stream()
                .anyMatch(item -> item.getQuantity() == 2)).isTrue();
    }

    @Test
    @DisplayName("삭제 테스트 2")
    void del() {
        transactionTemplate.execute(status -> {
            CartItemDto willDeleteItem = cartService.addCartItem(testCustomerId, addNewCartItem1());
            CartItemDto remainingItem = cartService.addCartItem(testCustomerId, addNewCartItem2());

            Long willDeleteItemId = willDeleteItem.getItemId(); // 삭제될 항목의 ID

            cartService.deleteCartItem(testCustomerId, willDeleteItemId); // 1번 상품을 삭제할 예정


            entityManager.flush();
            entityManager.clear();

            Cart cartAfterDeletion = entityManager.createQuery("select c from Cart c join fetch c.items where c.customer.id = :customerId", Cart.class)
                    .setParameter("customerId", testCustomerId)
                    .getSingleResult();

            List<CartItemDto> remainingItemDto = cartAfterDeletion.getItems().stream()
                    .map(CartItemDto::from)
                    .collect(Collectors.toList());

            assertEquals(1, remainingItemDto.size());
            assertEquals(remainingItem, remainingItemDto.get(0));

            return null;
        });
    }

    private CartItemDto addNewCartItem1() {
        return CartItemDto.builder()
                .productId(testProduct1Id)
                .quantity(1)
                .build();
    }

    private CartItemDto addNewCartItem2() {
        return CartItemDto.builder()
                .productId(testProduct2Id)
                .quantity(1)
                .build();
    }
}
