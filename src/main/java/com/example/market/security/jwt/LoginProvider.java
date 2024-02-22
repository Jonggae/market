package com.example.market.security.jwt;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
//인증과정 두번째 AuthenticationProvider
@Component
public class LoginProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    public LoginProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String customerName = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDetails userDetails = userDetailsService.loadUserByUsername(customerName);

        if (new BCryptPasswordEncoder().matches(password, userDetails.getPassword())) {
            return new UsernamePasswordAuthenticationToken(customerName, password, userDetails.getAuthorities());
        } else {
            throw new BadCredentialsException("로그인 실패 " + customerName);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
