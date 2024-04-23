package com.example.market.security.config;

import com.example.market.security.handler.jwt.JwtAccessDeniedHandler;
import com.example.market.security.handler.jwt.JwtAuthenticationEntryPoint;
import com.example.market.security.handler.login.LoginFailureHandler;
import com.example.market.security.handler.login.LoginSuccessHandler;
import com.example.market.security.jwt.JwtAuthenticationFilter;
import com.example.market.security.jwt.JwtAuthorizationFilter;
import com.example.market.security.jwt.JwtFilter;
import com.example.market.security.jwt.TokenProvider;
import com.example.market.security.misc.LoginProvider;
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
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final LoginProvider loginProvider;
    private final LoginSuccessHandler successHandler;
    private final LoginFailureHandler failureHandler;
    private final JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return loginProvider;
    }

    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(loginProvider, successHandler, failureHandler);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(tokenProvider, jwtFilter);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors();

        http.csrf(AbstractHttpConfigurer::disable);

        // 예외처리 필터 등록
        http.exceptionHandling(exceptionHandling ->
                exceptionHandling
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint));

        http.sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests((authorizeHttpRequest -> authorizeHttpRequest
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .antMatchers("/api/customer/**").permitAll()
                .antMatchers("/api/products/**").permitAll()
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()));

        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class); //JWT 인증을 위한 필터

        http.apply(new JwtSecurityConfig(tokenProvider)); // JWT 유효성 검증을 위한 필터

        return http.build();
    }

}
