package com.example.market.handler.login;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//ID , Password 를 입력하는 로그인 시도가 실패하였을 때 -> id, 비밀번호가 db와 일치하지 않을 때
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        String jsonPayload = "{\"message\": \"" + exception.getMessage() + "\", \"error\": \"Id, 비밀번호를 확인해 주세요\"}";
        response.getWriter().write(jsonPayload);
    }
}
