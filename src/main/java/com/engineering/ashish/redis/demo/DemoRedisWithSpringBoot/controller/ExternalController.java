package com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/external")
public class ExternalController {

    @GetMapping("/resource")
    public ResponseEntity<Map<String, String>> getExternalResource() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "API Key Validated. Access Granted!");
        return ResponseEntity.ok(response);
    }
}
