package com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.filter;

import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.service.RateLimitingService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;



@Slf4j
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitingService rateLimitingService;

    public RateLimitFilter(RateLimitingService rateLimitingService) {
        this.rateLimitingService = rateLimitingService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 🔥 Skip preflight CORS requests
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            chain.doFilter(request, response);
            return;
        }

        String clientIp = request.getRemoteAddr(); // Identify clients by IP
        log.info("🚀 Rate Limit Check for IP: {}", clientIp);

        if (!rateLimitingService.isAllowed(clientIp)) {
            log.warn("⚠️ Rate limit exceeded for IP: {}", clientIp);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("Retry-After", "60"); // Retry after 60 sec
            response.getWriter().write("Too many requests! Try again after 60 seconds.");
            return;
        }

        chain.doFilter(request, response);
    }
}
