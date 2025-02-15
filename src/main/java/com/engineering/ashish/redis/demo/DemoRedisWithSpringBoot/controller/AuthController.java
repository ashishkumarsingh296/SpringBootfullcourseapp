package com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.controller;

import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.Entity.User;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.cache.UserData;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.dto.UserDTO;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.exceptionhandling.InvalidCredentialsException;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.exceptionhandling.UserNotFoundException;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User request) {
        logger.info("Registering user: {}", request.getEmail());

        User user = authService.register(request);

        UserDTO response = UserDTO.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .userType(user.getUserType())
                .apiKey(user.getApiKey())  // Include API key in response
                .build();

        logger.info("User registered successfully: {}", user.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User request) {
        try {
            logger.info("Login attempt for user: {}", request.getEmail());

            Map<String, String> token = authService.login(request.getEmail(), request.getPassword());

            logger.info("Login successful for user: {}", request.getEmail());
            return ResponseEntity.ok(token);
        } catch (InvalidCredentialsException ex) {
            logger.warn("Invalid login attempt for user: {}", request.getEmail());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized");
            errorResponse.put("message", ex.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String refreshToken = request.get("refreshToken");

        logger.info("Refreshing access token for user: {}", email);

        String newAccessToken = authService.refreshAccessToken(email, refreshToken);

        logger.info("Access token refreshed successfully for user: {}", email);
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        logger.info("Logging out user: {}", email);

        authService.logout(email);

        logger.info("User {} logged out successfully", email);
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/st")
    public String hello() {
        logger.info("Health check API called.");
        return "hello";
    }

    @GetMapping("/user-data/{email}")
    public ResponseEntity<?> getUserData(@PathVariable String email) {
        logger.info("Fetching user data for: {}", email);

        UserData userData = authService.getUserDataFromCache(email);

        if (userData == null) {
            logger.warn("User data not found in cache for: {}", email);
            throw new UserNotFoundException("User with email: " + email + " does not exist in cache.");
        }

        logger.info("User data found in cache for: {}", email);
        return ResponseEntity.ok(userData);
    }
}
