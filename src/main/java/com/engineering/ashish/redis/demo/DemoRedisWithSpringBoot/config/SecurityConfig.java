package com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;


@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/public/**").permitAll()  // Public access
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Admin-only routes
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN") // User and Admin access
                        .anyRequest().authenticated() // Default: all other routes require authentication
                );

        return http.build();
    }

}
