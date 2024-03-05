package com.example.market.customer;

import com.example.market.cart.repository.CartRepository;
import com.example.market.customer.dto.AuthorityDto;
import com.example.market.customer.dto.CustomerDto;
import com.example.market.customer.service.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@DisplayName("회원 가입 시 장바구니 생성 테스트")
public class CustomerCartCreationTest {
    @Autowired
    protected CustomerService customerService;
    @Autowired
    private CartRepository cartRepository;

    @Test
    @Transactional
    @DisplayName("회원 가입 시 장바구니 생성 확인")
    void whenRegisterThenCartIsCreated() {
        AuthorityDto authority = AuthorityDto.builder()
                .authorityName("ROLE_USER")
                .build();

        CustomerDto customerDto = CustomerDto.builder()
                .customerName("customer")
                .password("password")
                .email("test@example.com")
                .phoneNumber("01012345678")
                .authorityDtoSet(Collections.singleton(authority))
                .build();

        CustomerDto registeredCustomer = customerService.register(customerDto);

        boolean cartExists = cartRepository.findByCustomerId(registeredCustomer.getId()).isPresent();
        assertThat(cartExists).isTrue();
    }
}
