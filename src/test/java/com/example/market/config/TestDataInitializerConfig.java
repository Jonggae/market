package com.example.market.config;

import com.example.market.customer.entity.Authority;
import com.example.market.customer.entity.Customer;
import com.example.market.customer.repository.AuthorityRepository;
import com.example.market.customer.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@Configuration
@Profile("test")
public class TestDataInitializerConfig {
    @Bean
    public CommandLineRunner initData(CustomerRepository customerRepository, AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println("***********Initializing test data...");

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

            customerRepository.save(customer);
            System.out.println("*************Test data initialization complete.");

        };
    }
}
