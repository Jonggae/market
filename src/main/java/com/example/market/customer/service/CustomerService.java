package com.example.market.customer.service;

import com.example.market.customer.dto.CustomerDto;
import com.example.market.customer.entity.Authority;
import com.example.market.customer.entity.Customer;
import com.example.market.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerDto register(CustomerDto customerDto) {
        checkUserInfo(customerDto.getCustomerName(), customerDto.getEmail(), customerDto.getPhoneNumber());

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER").build();

        Customer customer = Customer.builder()
                .customerName(customerDto.getCustomerName())
                .password(passwordEncoder.encode(customerDto.getPassword()))
                .email(customerDto.getEmail())
                .phoneNumber(customerDto.getPhoneNumber())
                .authorities(Collections.singleton(authority)) // 특정한 요소를 가진 불변한 컬렉션
                .build();

        return CustomerDto.from(customerRepository.save(customer));
    }

    private void checkUserInfo(String customerName, String email, String phoneNumber) {
        if (customerRepository.findByCustomerName(customerName).isPresent()) {
            throw new IllegalArgumentException("이미 사용중인 ID 입니다");
        }
        // 이메일 중복 체크
        if (customerRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 사용중인 이메일 주소입니다");
        }

        // 전화번호 중복 체크
        if (customerRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new IllegalArgumentException("이미 사용중인 전화번호입니다");
        }
    }
}
