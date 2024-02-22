package com.example.market.etc;

import com.example.market.customer.entity.Authority;
import com.example.market.customer.entity.Customer;
import com.example.market.customer.repository.AuthorityRepository;
import com.example.market.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

// customer로 표현되었지만 admin 권한을 가진 관리자임
@Component
public class DataInitializer implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(CustomerRepository customerRepository, AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... arg) {

        Authority adminAuthority = authorityRepository.save(new Authority("ROLE_ADMIN"));

        if (!customerRepository.existsByCustomerName("admin")) {
            Customer admin = Customer.builder()
                    .customerName("admin")
                    .password(passwordEncoder.encode("admin"))
                    .phoneNumber("0101234578")
                    .email("admin@admin.com")
                    .authorities(Collections.singleton(adminAuthority))
                    .build();
            customerRepository.save(admin);
        }
    }
}
