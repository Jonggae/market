package com.example.market.customer.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorityDto {
    public String authorityName;
    // ROLE_USER , ROLE_ADMIN
}
