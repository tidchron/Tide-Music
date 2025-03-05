package com.example.tidemusic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()  // 모든 요청에 대해 인증 없이 접근 허용
                )
                .csrf(csrf -> csrf.disable());  // 필요에 따라 CSRF 비활성화
        return http.build();
    }
}
