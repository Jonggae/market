package com.example.market.customer.dto;

import com.example.market.customer.entity.Authority;
import com.example.market.customer.entity.Customer;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {
    private Long id;
    private String customerName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String phoneNumber;
    private String email;

    private Set<AuthorityDto> authorityDtoSet;

    public static CustomerDto from(Customer customer) {
        if (customer == null) return null;

        return CustomerDto.builder()
                .id(customer.getId())
                .customerName(customer.getCustomerName())
                .phoneNumber(customer.getPhoneNumber())
                .email(customer.getEmail())
                .authorityDtoSet(customer.getAuthorities().stream()
                        .map(authority -> AuthorityDto.builder().authorityName(authority.getAuthorityName()).build())
                        .collect(Collectors.toSet()))

                .build();
    }

    public static Customer toEntity(CustomerDto customerDto) {
        if (customerDto == null) return null;

        return Customer.builder()
                .customerName(customerDto.getCustomerName())
                .phoneNumber(customerDto.getPhoneNumber())
                .email(customerDto.getEmail())
                .authorities(customerDto.getAuthorityDtoSet().stream()
                        .map(authorityDto -> Authority.builder().authorityName(authorityDto.getAuthorityName()).build())
                        .collect(Collectors.toSet()))
                .build();
    }
}
