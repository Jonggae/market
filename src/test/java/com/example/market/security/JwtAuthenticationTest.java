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

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/customer/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerName\":\"" + customerName + "\", \"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn(); // MvcResult 객체를 반환

        // 응답 본문에서 JWT 토큰 추출
        String responseBody = mvcResult.getResponse().getContentAsString();
        String token = JsonPath.parse(responseBody).read("$.token");

        System.out.println("Extracted JWT Token: " + token); // 콘솔에 토큰 출력

    }

    @Test
    @DisplayName("로그인 실패 시 401 반환 테스트")
    public void loginFail() throws Exception {
        String customerName = "testtestUser";
        String password = "passpassowrd";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/customer/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerName\":\"" + customerName + "\", \"password\":\"" + password + "\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그인 후 접근할 수 있는 페이지 접근 - 200 Ok")
    public void test1() throws Exception {

        String customerName = "testUser";
        String password = "password";

        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/customer/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerName\":\"" + customerName + "\", \"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        // 응답 본문에서 JWT 토큰 추출
        String responseBody = loginResult.getResponse().getContentAsString();
        String token = JsonPath.parse(responseBody).read("$.token");

        // 페이지 접근 요청에 JWT 토큰 포함하여 보내기
        mockMvc.perform(MockMvcRequestBuilders.get("/api/customer/my-info")
                        .header("Authorization", "Bearer " + token)) // JWT 토큰 포함
                .andExpect(status().isOk()) // 인증된 사용자의 경우 200 OK 반환
                .andExpect(jsonPath("$.customerName").exists()); // 반환되는 고객 정보가 존재하는지 확인
    }

}
