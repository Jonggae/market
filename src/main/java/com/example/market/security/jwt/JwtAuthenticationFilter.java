package com.example.market.security.jwt;

import com.example.market.security.handler.login.LoginFailureHandler;
import com.example.market.security.handler.login.LoginSuccessHandler;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Basic ")) {

            String base64Credentials = header.substring("Basic ".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);

            final String[] values = credentials.split(":", 2);

            if (values.length == 2) {
                String customerName = values[0];
                String password = values[1];
                Authentication authenticationToken = new UsernamePasswordAuthenticationToken(customerName, password);
                return loginProvider.authenticate(authenticationToken);
            }
        }
        throw new AuthenticationServiceException("로그인 실패");

    }
}

