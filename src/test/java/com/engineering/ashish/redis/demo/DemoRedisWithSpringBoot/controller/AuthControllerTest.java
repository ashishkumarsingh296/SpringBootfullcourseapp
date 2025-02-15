package com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.controller;

import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.Entity.User;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.cache.UserData;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.exceptionhandling.InvalidCredentialsException;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.dto.UserDTO;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.exceptionhandling.UserNotFoundException;

import java.util.Map;


@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void testRegister() {
        // Arrange
        User user = new User();
        user.setEmail("test11user@example.com");
        user.setPassword("password");
        when(authService.register(any(User.class))).thenReturn(user);

        // Act
        ResponseEntity<?> response = authController.register(user);

        // Assert
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UserDTO);
        verify(authService).register(user);
    }

    @Test
    void testLogin_Success() {
        // Arrange
        String token = "mocked-jwt-token";
        User user = new User();
        user.setEmail("test11user@example.com");
        user.setPassword("password");
        when(authService.login(user.getEmail(), user.getPassword())).thenReturn(token);

        // Act
        ResponseEntity<?> response = authController.login(user);

        // Assert
        assertEquals(token, response.getBody());
        verify(authService).login(user.getEmail(), user.getPassword());
    }

    @Test
    void testLogin_Failure() {
        // Arrange
        User user = new User();
        user.setEmail("wrong@example.com");
        user.setPassword("wrongpassword");

        when(authService.login(user.getEmail(), user.getPassword()))
                .thenThrow(new InvalidCredentialsException("Invalid Credentials"));

        // Act
        ResponseEntity<?> response = authController.login(user);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);

        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertEquals("Unauthorized", responseBody.get("error"));
        assertEquals("Invalid Credentials", responseBody.get("message"));

        verify(authService).login(user.getEmail(), user.getPassword());
    }


    @Test
    void testGetUserData_Success() {
        // Arrange
        UserData userData = new UserData("test@example.com", "Test User");
        when(authService.getUserDataFromCache("test@example.com")).thenReturn(userData);

        // Act
        ResponseEntity<?> response = authController.getUserData("test@example.com");

        // Assert
        assertNotNull(response.getBody());
        assertEquals(userData, response.getBody());
        verify(authService).getUserDataFromCache("test@example.com");
    }

    @Test
    void testGetUserData_UserNotFound() {
        // Arrange
        when(authService.getUserDataFromCache("notfound@example.com")).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(UserNotFoundException.class, () -> authController.getUserData("notfound@example.com"));
        assertTrue(exception.getMessage().contains("User with email: notfound@example.com does not exist in cache."));
        verify(authService).getUserDataFromCache("notfound@example.com");
    }
}
