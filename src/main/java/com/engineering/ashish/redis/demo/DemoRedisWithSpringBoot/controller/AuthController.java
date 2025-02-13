package com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.controller;

import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.Entity.User;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.cache.UserData;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.exceptionhandling.InvalidCredentialsException;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.exceptionhandling.UserNotFoundException;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User request) {
        String response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User request) {
        try {
            String token = authService.login(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(token);  // Return token if login is successful
        } catch (InvalidCredentialsException ex) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized");
            errorResponse.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);  // Return 401 if invalid credentials
        }
    }

    @GetMapping("/st")
    public String hello() {
        return "hello";
    }

    @GetMapping("/user-data/{email}")
    public ResponseEntity<?> getUserData(@PathVariable String email) {
        UserData userData = authService.getUserDataFromCache(email);

        if (userData == null) {
            throw new UserNotFoundException("User with email: " + email + " does not exist in cache.");
        }

        return ResponseEntity.ok(userData);
    }

}
