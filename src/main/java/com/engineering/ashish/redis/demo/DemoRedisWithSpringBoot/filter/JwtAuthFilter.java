package com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.filter;


import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

//@Component
//public class JwtAuthFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws ServletException, IOException {
//
//        String authHeader = request.getHeader("Authorization");
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        String token = authHeader.substring(7); // Remove "Bearer " prefix
//
//        try {
//            String username = jwtUtil.extractUsername(token);
//
//            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                UsernamePasswordAuthenticationToken authenticationToken =
//                        new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
//                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//            }
//
//        } catch (ExpiredJwtException | SignatureException | MalformedJwtException e) {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT token");
//            return;
//        }
//
//        chain.doFilter(request, response);
//    }
//}
//
//

@Slf4j
@Component
@Order(2)
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @PostConstruct
    public void init() {
        System.out.println("🚀 JwtAuthFilter Registered");
    }

    public JwtAuthFilter() {
        log.info("🚀 JwtAuthFilter Loaded!");
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix

        try {
            String username = jwtUtil.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

        } catch (ExpiredJwtException | SignatureException | MalformedJwtException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT token");
            return;
        }

        chain.doFilter(request, response);
    }
}