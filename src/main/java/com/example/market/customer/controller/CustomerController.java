package com.example.market.customer.controller;

import com.example.market.commons.apiResponse.ApiResponseDto;
import com.example.market.commons.apiResponse.ApiResponseUtil;
import com.example.market.customer.dto.CustomerDto;
import com.example.market.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto<CustomerDto>> register(@RequestBody CustomerDto requestDto) {
        CustomerDto registerdCustomerDto = customerService.register(requestDto);
        String successMessage = "회원 가입이 성공적으로 완료되었습니다.";
        return ApiResponseUtil.success(successMessage, registerdCustomerDto, 200);
    }

    // 로그인 한 개인이 접근 가능한 내 정보 페이지
    @GetMapping("/my-info")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponseDto<CustomerDto>> getCustomerInfo() {
        CustomerDto getCustomerDto = customerService.getCustomerInfo();
        String successMessage = "해당 유저의 정보입니다";
        return ApiResponseUtil.success(successMessage, getCustomerDto, 200);
    }
}
