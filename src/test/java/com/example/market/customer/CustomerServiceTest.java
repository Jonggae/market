package com.example.market.customer;

import com.example.market.customer.dto.AuthorityDto;
import com.example.market.customer.dto.CustomerDto;
import com.example.market.customer.entity.Customer;
import com.example.market.customer.repository.AuthorityRepository;
import com.example.market.customer.repository.CustomerRepository;
import com.example.market.customer.service.CustomerService;
import com.example.market.security.config.SecurityConfig;
import com.example.market.exception.DuplicateMemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("회원 관련 서비스 테스트")
@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthorityRepository authorityRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    @DisplayName("회원 가입 서비스 테스트")
    void registerServiceTest() {
        when(customerRepository.findByCustomerName(Mockito.anyString())).thenReturn(Optional.empty());
        when(customerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
        when(customerRepository.findByPhoneNumber(Mockito.anyString())).thenReturn(Optional.empty());

        when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encodedPassword");

        CustomerDto customerDto = createTestCustomerDto();

        customerService.register(customerDto);

        Mockito.verify(customerRepository, Mockito.times(1)).save(any(Customer.class));

    }

    @Test
    @DisplayName("id 중복 회원 있을때 - CustomerName")
    void registerServiceWithExistingCustomerName() {

        when(customerRepository.findByCustomerName(Mockito.anyString())).thenReturn(Optional.of(new Customer()));

        AuthorityDto authority = AuthorityDto.builder()
                .authorityName("ROLE_USER")
                .build();

        CustomerDto customerDto = CustomerDto.builder()
                .customerName("existingUser")
                .password("password")
                .email("existingUser@example.com")
                .phoneNumber("01012345678")
                .authorityDtoSet(Collections.singleton(authority))
                .build();

        assertThrows(DuplicateMemberException.class, () -> customerService.register(customerDto));
    }


    private CustomerDto createTestCustomerDto() {

        AuthorityDto authority = AuthorityDto.builder()
                .authorityName("ROLE_USER").build();

        return CustomerDto.builder()
                .customerName("CJW")
                .password("1234")
                .email("CJW@mail.com")
                .phoneNumber("01012345678")
                .authorityDtoSet(Collections.singleton(authority))
                .build();

    }
}