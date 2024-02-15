package com.example.market.security.utils;

import com.example.market.security.jwt.TokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

//OncePerRequestFilter 는 인증 시 한번만 실행되는 필터

public class BasicAuthenticationFilter extends OncePerRequestFilter {

    private AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public BasicAuthenticationFilter(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, TokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws SecurityException, IOException, ServletException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Basic ")) {
            String[] tokens = extractAndDecodedHeader(header);
            assert tokens.length == 2;

            String customerName = tokens[0];
            String password = tokens[1];

            // UsernamePasswordAuthenticationToken 생성
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(customerName, password);

            try {
                // AuthenticationManager를 사용하여 인증 시도
                Authentication authentication = authenticationManager.authenticate(authenticationToken);

                // 인증 성공 시, SecurityContext에 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 인증 성공시 jwt 토큰 생성 및 응답 헤더 설정
                String jwt = tokenProvider.createToken(authentication);
                response.setHeader("Authorization", "Bearer " + jwt);
            } catch (AuthenticationException e) {
                // 인증 실패 처리
                SecurityContextHolder.clearContext();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String[] extractAndDecodedHeader(String header) {
        byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(base64Token);
            String token = new String(decoded, StandardCharsets.UTF_8);

            int delim = token.indexOf(":");
            if (delim == -1) {
                throw new RuntimeException("Basic 토큰이 유효하지 않습니다.");
            }
            return new String[]{token.substring(0, delim), token.substring(delim + 1)};
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Basic 토큰 해석에 실패하였습니다");
        }
    }

}

