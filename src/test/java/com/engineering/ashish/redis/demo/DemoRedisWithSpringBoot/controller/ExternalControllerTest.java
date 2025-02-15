package com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ExternalControllerTest {

    @InjectMocks
    private ExternalController externalController;

    @Test
    void testGetExternalResource() {
        // Act
        ResponseEntity<Map<String, String>> response = externalController.getExternalResource();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("API Key Validated. Access Granted!", response.getBody().get("message"));
    }
}
