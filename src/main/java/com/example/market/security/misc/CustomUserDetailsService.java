package com.example.market.security.misc;

import com.example.market.customer.entity.Customer;
import com.example.market.customer.repository.CustomerRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    public CustomUserDetailsService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override //LoginProvider 에서 이 메서드를 사용함
    public UserDetails loadUserByUsername(String customerName) throws UsernameNotFoundException {
        return customerRepository.findOneWithAuthoritiesByCustomerName(customerName)
                .map(this::createCustomer)
                .orElseThrow(() -> new UsernameNotFoundException(customerName + "데이터베이스에서 찾을 수 없습니다."));
    }

    private User createCustomer(Customer customer) {
        List<GrantedAuthority> grantedAuthorities = customer.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toList());
        return new User(customer.getCustomerName(),
                customer.getPassword(), grantedAuthorities);

    }
}
