package com.example.market.security.jwt;

import com.example.market.customer.dto.LoginRequestDto;
import com.example.market.security.handler.LoginFailureHandler;
import com.example.market.security.handler.LoginSuccessHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
// 인증과정 첫번째 UsernamePasswordAuthenticationFilter

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final LoginProvider loginProvider;

    public JwtAuthenticationFilter(LoginProvider loginProvider, LoginSuccessHandler successHandler, LoginFailureHandler failureHandler) {
        super();
        this.loginProvider = loginProvider;
        // 성공 핸들러와 실패 핸들러 설정
        this.setAuthenticationSuccessHandler(successHandler);
        this.setAuthenticationFailureHandler(failureHandler);

        // 로그인 URL 설정
        setFilterProcessesUrl("/api/customer/login");
    }

    @Override //로그인 시도
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            LoginRequestDto loginRequestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    loginRequestDto.getCustomerName(),
                    loginRequestDto.getPassword());
            return loginProvider.authenticate(authentication);
            //LoginProvider 로 넘어감


        } catch (IOException e) {
            throw new AuthenticationServiceException("로그인 실패", e);
        }
    }
}

