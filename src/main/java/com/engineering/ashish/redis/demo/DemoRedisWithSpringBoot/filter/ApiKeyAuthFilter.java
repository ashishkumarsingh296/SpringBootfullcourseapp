package com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.filter;

import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.Entity.ApiKey;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.repository.APIKeyRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

//@Component
//public class ApiKeyAuthFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private APIKeyRepository apiKeyRepository;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws ServletException, IOException {
//
//        if (!request.getRequestURI().startsWith("/external/")) {
//            chain.doFilter(request, response);
//            return; // Skip API Key validation for non-external requests
//        }
//        String apiKey = request.getHeader("X-API-KEY");
//
//        if (apiKey == null || apiKey.isEmpty()) {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing API Key");
//            return;
//        }
//
//        Optional<ApiKey> apiKeyOptional = apiKeyRepository.findByApiKey(apiKey);
//
//        if (apiKeyOptional.isEmpty()) {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
//            return;
//        }
//
//        chain.doFilter(request, response);
//    }
//}


    @Slf4j
    @Component
    @Order(1) // Ensure API Key filter runs first
    public class ApiKeyAuthFilter extends OncePerRequestFilter {

        @Autowired
        private APIKeyRepository apiKeyRepository;

        @PostConstruct
        public void init() {
            System.out.println("🚀 ApiKeyAuthFilter Registered");
        }

        public ApiKeyAuthFilter() {
            log.info("🚀 ApiKeyAuthFilter Loaded!");
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws ServletException, IOException {

            // Only process API Key authentication for /external/** endpoints
            if (!request.getRequestURI().startsWith("/external/")) {
                chain.doFilter(request, response);
                return; // Skip API Key validation for non-external requests
            }

            String apiKey = request.getHeader("X-API-KEY");

            if (apiKey == null || apiKey.isEmpty()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing API Key");
                return;
            }

            Optional<ApiKey> apiKeyOptional = apiKeyRepository.findByApiKey(apiKey);

            if (apiKeyOptional.isEmpty()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
                return;
            }

            chain.doFilter(request, response);
        }
    }