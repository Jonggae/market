package com.example.market.dataConfig;

import com.example.market.cart.entity.Cart;
import com.example.market.cart.repository.CartRepository;
import com.example.market.customer.entity.Authority;
import com.example.market.customer.entity.Customer;
import com.example.market.customer.repository.AuthorityRepository;
import com.example.market.customer.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@TestConfiguration
@Profile("test")
public class TestDataInitializerConfig {
    @Bean
    public CommandLineRunner initData(CustomerRepository customerRepository, AuthorityRepository authorityRepository, CartRepository cartRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            Authority authority = Authority.builder()
                    .authorityName("ROLE_USER").build();
            authorityRepository.save(authority);

            Customer customer = Customer.builder()
                    .customerName("testUser")
                    .password(passwordEncoder.encode("password"))
                    .phoneNumber("01012345678")
                    .email("test@example.com")
                    .authorities(Collections.singleton(authority)) // authorities는 Set<Authority> 타입의 객체여야 함
                    .build();
            Customer savedCustomer = customerRepository.save(customer);

            // 장바구니 생성
            Cart cart = Cart.builder()
                    .customer(savedCustomer)
                    .build();
            cartRepository.save(cart);

        };
    }
}
