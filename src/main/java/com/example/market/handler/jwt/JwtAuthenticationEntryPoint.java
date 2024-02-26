package com.example.market.handler.jwt;

import com.example.market.security.dto.ErrorDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    /*
    인증되지 않은 접근 401 (JWT 토큰이 유효하지 않을 때)
    * 서명, 잘못된 토큰 등등 */

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        ErrorDto errorDTO = new ErrorDto(HttpStatus.UNAUTHORIZED.value(), "잘못된 인증 정보입니다.");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorDTO));
    }
}
