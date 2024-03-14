package com.example.market.security.handler.jwt;

import com.example.market.commons.apiResponse.ApiResponseDto;
import com.example.market.commons.apiResponse.ApiResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    // JWT 인증 값에 [권한]이 없는 접근을 할 때 403
    // eg) admin 권한을 customer 가 접근 하였을 때 Security 레벨

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ResponseEntity<ApiResponseDto<Object>> errorResponse = ApiResponseUtil.error(
                "접근 권한이 없습니다", 403, "ACCESS_DENIED", "해당 작업은 관리자만 가능합니다");

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}
