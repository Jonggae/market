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
@DisplayName("JWT 관련 테스트")
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

    @Test
    @DisplayName("로그인 실패 시 401 반환 테스트")
    public void loginFail() throws Exception{
        String customerName = "testtestUser";
        String password = "passpassowrd";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/customer/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerName\":\"" + customerName + "\", \"password\":\"" + password + "\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그인 후 접근할 수 있는 페이지 접근")
    public void test1() {

    }

}
