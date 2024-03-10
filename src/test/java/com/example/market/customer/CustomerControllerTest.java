package com.example.market.customer;

import com.example.market.commons.apiResponse.ApiResponseDto;
import com.example.market.customer.controller.CustomerController;
import com.example.market.customer.dto.AuthorityDto;
import com.example.market.customer.dto.CustomerDto;
import com.example.market.customer.service.CustomerService;
import com.example.market.security.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("회원 관련 컨트롤러")
@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;
    @InjectMocks
    private CustomerController customerController;

    @Test
    @DisplayName("회원 가입 컨트롤러 테스트")
    public void register() throws Exception {
       // given
        CustomerDto testCustomerDto = createTestCustomerDto();
        when(customerService.register(any())).thenReturn(testCustomerDto);

        //when
        ResponseEntity<ApiResponseDto<CustomerDto>> responseEntity = customerController.register(testCustomerDto);

        //then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        verify(customerService, times(1)).register(eq(testCustomerDto));
    }


    // CustomerDto 생성
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