package com.example.market.customer.repository;

import com.example.market.customer.entity.Customer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByCustomerName(String customerName);
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByPhoneNumber(String email);

    @EntityGraph(attributePaths = "authorities")
    Optional<Customer> findOneWithAuthoritiesByCustomerName(String customerName);


}
