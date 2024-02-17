package com.example.market.security;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BasicAuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Basic header를 받아 로그인 시 JWT 발급되는지 테스트")
    public void basicHeaderTest() throws Exception {
        String encodedCredentials = Base64.getEncoder().encodeToString("testUser:password".getBytes());

        MvcResult mvcResult = mockMvc.perform(post("/api/customer/login")
                        .header("Authorization", "Basic " + encodedCredentials)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        String token = JsonPath.parse(responseBody).read("$.token", String.class); // 토큰 값 추출
        System.out.println("Extracted JWT Token: " + token); // 콘솔에 토큰 출력

        assertNotNull(token);
    }
}
