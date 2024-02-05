package com.example.market.customer.controller;

import com.example.market.customer.dto.CustomerDto;
import com.example.market.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<CustomerDto> register(@RequestBody CustomerDto requestDto) {
        return ResponseEntity.ok(customerService.register(requestDto));
    }
}
