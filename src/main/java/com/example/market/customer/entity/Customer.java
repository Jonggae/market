package com.example.market.customer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer")
public class Customer {

    @Id
    @Column(name = "customer_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "customer_name", unique = true, nullable = false)
    private String customerName;

    @Column(name = "customer_password", nullable = false)
    private String password;

    @Column(name = "customer_phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "customer_email", nullable = false)
    private String email;

    @ManyToMany
    @JoinTable(name = "customer_authority", joinColumns = {@JoinColumn(name = "customer_id", referencedColumnName = "customer_id")},
    inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities;

}

//    todo: order, cart 와 연결관게 설정하기
