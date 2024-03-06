package com.example.market.customer.controller;

import com.example.market.customer.dto.CustomerDto;
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
    public ResponseEntity<CustomerDto> register(@RequestBody CustomerDto requestDto) {
        return ResponseEntity.ok(customerService.register(requestDto));
    }

    // 로그인 한 개인이 접근 가능한 내 정보 페이지
    @GetMapping("/my-info")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public CustomerDto getCustomerInfo() {
        return customerService.getCustomerInfo();
    }
}
