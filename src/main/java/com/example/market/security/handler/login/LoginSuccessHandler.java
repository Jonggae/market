package com.example.market.security.handler.login;

import com.example.market.security.jwt.TokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//로그인 시 id, password가 db에 저장된 값과 일치하여 로그인이 성공 하였을 때. Jwt 토큰이 생성됨.
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;

    public LoginSuccessHandler(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String jwt = tokenProvider.createToken(authentication);
//        response.addHeader("Authorization", "Bearer " + jwt);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jwt);
        response.setStatus(HttpServletResponse.SC_OK);
        /*
         * 프론트 페이지 작성을 위해 아래 API테스트 용 데이터는 주석 처리 하였음. */
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write("{\"token\":\"" + jwt + "\"}");
//        response.setStatus(HttpServletResponse.SC_OK);
    }
}
