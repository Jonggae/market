package com.example.market.security.config;

import com.example.market.security.handler.LoginFailureHandler;
import com.example.market.security.handler.LoginSuccessHandler;
import com.example.market.security.jwt.JwtAccessDeniedHandler;
import com.example.market.security.jwt.JwtAuthenticationEntryPoint;
import com.example.market.security.jwt.LoginProvider;
import com.example.market.security.jwt.TokenProvider;
import com.example.market.security.utils.BasicAuthenticationFilter;
import com.example.market.security.utils.CustomUserDetailsService;
import com.example.market.security.utils.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final LoginProvider loginProvider;
    private final LoginSuccessHandler successHandler;
    private final LoginFailureHandler failureHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(tokenProvider, loginProvider, successHandler, failureHandler);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public BasicAuthenticationFilter basicAuthenticationFilter(AuthenticationManager authenticationManager) {
        return new BasicAuthenticationFilter(authenticationManager, userDetailsService, passwordEncoder(), tokenProvider);

    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return loginProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable); //csrf 비활성화는 나중에 신경써야할듯?

        // 예외처리 필터 등록
        http.exceptionHandling(exceptionHandling ->
                exceptionHandling
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint));

        http.sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests((authorizeHttpRequest -> authorizeHttpRequest
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .antMatchers("/api/customer/register", "/api/customer/login").permitAll()
                .anyRequest().authenticated()));

        http.authenticationProvider(loginProvider);

        //필터 순서를 고려해야?
        http.addFilterBefore(basicAuthenticationFilter(authenticationManager(authenticationConfiguration)), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); //JWT 로그인 과정

        http.apply(new JwtSecurityConfig(tokenProvider)); // 다른 버전에 유의


        return http.build();
    }

}
