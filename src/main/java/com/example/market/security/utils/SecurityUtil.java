package com.example.market.security.utils;

import com.example.market.customer.entity.Customer;
import com.example.market.customer.repository.CustomerRepository;
import com.example.market.exception.NotFoundMemberException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

// 아래 getCurrentUsername() 메서드를 이용하여 인증된 Authentication 객체 내부의 요소들을 가져옴 (eg. customerName 같은)
@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final CustomerRepository customerRepository;

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);


    // 로그인 후 SecurityContextHolder에서 정보를 가져옴
    public Optional<String> getCurrentCustomerName() {
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
        Customer customer = customerRepository.findByCustomerName(customerName)
                .orElseThrow(NotFoundMemberException::new);
        return customer.getId();
    }
}
