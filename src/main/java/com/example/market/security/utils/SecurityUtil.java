package com.example.market.security.utils;

import com.example.market.customer.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

// 아래 getCurrentUsername() 메서드를 이용하여 인증된 Authentication 객체 내부의 요소들을 가져옴 (eg. customerName 같은)
@Component
public class SecurityUtil {
    private CustomerService customerService;

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);


    // 로그인 후 SecurityContextHolder에서 정보를 가져옴
    public static Optional<String> getCurrentCustomerName() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            logger.debug("Security context에 인증 정보가 없습니다.");
            return Optional.empty();
        }

        String username = null;

        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            username = springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            username = (String) authentication.getPrincipal();
        }
        return Optional.ofNullable(username);
    }

    public Long getCurrentCustomerId(Authentication authentication) {
        String customerName = authentication.getName();
        return customerService.findCustomerIdByCustomerName(customerName);
    }
}
