package com.example.market.customer.service;

import com.example.market.cart.entity.Cart;
import com.example.market.cart.repository.CartRepository;
import com.example.market.customer.dto.CustomerDto;
import com.example.market.customer.entity.Authority;
import com.example.market.customer.entity.Customer;
import com.example.market.customer.repository.AuthorityRepository;
import com.example.market.customer.repository.CustomerRepository;
import com.example.market.exception.DuplicateMemberException;
import com.example.market.exception.NotFoundMemberException;
import com.example.market.security.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartRepository cartRepository;

    public CustomerDto register(CustomerDto customerDto) {
        checkUserInfo(customerDto.getCustomerName(), customerDto.getEmail(), customerDto.getPhoneNumber());

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER").build();
        authorityRepository.save(authority);

        Customer customer = Customer.builder()
                .customerName(customerDto.getCustomerName())
                .password(passwordEncoder.encode(customerDto.getPassword()))
                .email(customerDto.getEmail())
                .phoneNumber(customerDto.getPhoneNumber())
                .authorities(Collections.singleton(authority)) // 특정한 요소를 가진 불변한 컬렉션
                .build();

        customer = customerRepository.save(customer);


        Cart cart = Cart.builder()
                .customer(customer)
                .build();
        cartRepository.save(cart);

        return CustomerDto.from(customer);
    }

    //유저 정보 표시하기
    public CustomerDto getCustomerInfo() {
        return CustomerDto.from(
                SecurityUtil.getCurrentCustomerName()
                        .flatMap(customerRepository::findOneWithAuthoritiesByCustomerName)
                        .orElseThrow(() -> new NotFoundMemberException("회원을 찾을 수 없습니다"))
        );
    }

    private void checkUserInfo(String customerName, String email, String phoneNumber) {
        if (customerRepository.findByCustomerName(customerName).isPresent()) {
            throw new DuplicateMemberException("이미 사용중인 ID 입니다");
        }
        // 이메일 중복 체크
        if (customerRepository.findByEmail(email).isPresent()) {
            throw new DuplicateMemberException("이미 사용중인 이메일 주소입니다");
        }

        // 전화번호 중복 체크
        if (customerRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new DuplicateMemberException("이미 사용중인 전화번호입니다");
        }
    }
}
