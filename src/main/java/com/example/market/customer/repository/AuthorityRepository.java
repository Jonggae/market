package com.example.market.customer.repository;

import com.example.market.customer.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<Authority, String> {

    Optional<Authority> findByAuthorityName(String roleUser);
}
