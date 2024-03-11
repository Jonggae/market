package com.example.market.customer.controller;

import com.example.market.commons.MessageUtil;
import com.example.market.commons.apiResponse.ApiResponseDto;
import com.example.market.commons.apiResponse.ApiResponseUtil;
import com.example.market.customer.dto.CustomerDto;
import com.example.market.customer.message.CustomerApiMessage;
import com.example.market.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto<CustomerDto>> register(@RequestBody CustomerDto requestDto) {
        CustomerDto registerdCustomerDto = customerService.register(requestDto);
        String message = MessageUtil.getMessage(CustomerApiMessage.CUSTOMER_REGISTER_SUCCESS);
        return ApiResponseUtil.success(message, registerdCustomerDto, 200);
    }

    // 로그인 한 개인이 접근 가능한 내 정보 페이지
    @GetMapping("/my-info")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponseDto<CustomerDto>> getCustomerInfo() {
        CustomerDto getCustomerDto = customerService.getCustomerInfo();
        String message = MessageUtil.getMessage(CustomerApiMessage.CUSTOMER_DETAIL_SUCCESS);
        return ApiResponseUtil.success(message, getCustomerDto, 200);
    }
}
