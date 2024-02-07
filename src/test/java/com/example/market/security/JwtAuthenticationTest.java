package com.example.market.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class JwtAuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("로그인 시 jwt 발급 되는지 테스트")
    public void whenLoginWithValidUser_thenReceiveJwtToken() throws Exception {
        String customerName = "testUser";
        String password = "password";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/customer/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerName\":\"" + customerName + "\", \"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

}
