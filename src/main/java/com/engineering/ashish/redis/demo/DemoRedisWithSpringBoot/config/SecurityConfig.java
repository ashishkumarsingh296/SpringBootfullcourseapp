package com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.config;

import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.filter.ApiKeyAuthFilter;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.filter.JwtAuthFilter;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.filter.RateLimitFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor

public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private ApiKeyAuthFilter apiKeyAuthFilter;

    @Autowired
    private RateLimitFilter rateLimitFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/public/**").permitAll()  // Public access
                        .requestMatchers("/admin/**").hasRole("ADMIN") // JWT Auth for Admin
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN") // JWT Auth for Users
                        .requestMatchers("/external/**").permitAll()  // API Key-based authentication
                        .anyRequest().authenticated()
                )

                // Apply API Key authentication first
                .addFilterBefore(apiKeyAuthFilter, UsernamePasswordAuthenticationFilter.class)
                // Apply JWT authentication after API Key authentication
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(rateLimitFilter, JwtAuthFilter.class); // Ensure RateLimitFilter runs AFTER authentication


        return http.build();
    }
}
