package com.example.market.product;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class ProductTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setup() {

    }
}
