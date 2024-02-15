package com.example.market.customer.dto;

import com.example.market.customer.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {
    private String customerName;
    private String password;

    public Customer toEntity() {
        return Customer.builder()
                .customerName(customerName)
                .password(password)
                .build();
    }
}
