package com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.controller;

import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.Entity.User;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.cache.UserData;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.dto.UserDTO;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.exceptionhandling.InvalidCredentialsException;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.exceptionhandling.UserNotFoundException;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("password123");
    }

    @Test
    void testRegister() {
        when(authService.register(any(User.class))).thenReturn(mockUser);

        ResponseEntity<?> response = authController.register(mockUser);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(authService, times(1)).register(any(User.class));
    }

    @Test
    void testLogin_Success() {
        Map<String, String> tokenResponse = new HashMap<>();
        tokenResponse.put("accessToken", "mockAccessToken");
        tokenResponse.put("refreshToken", "mockRefreshToken");
        when(authService.login(mockUser.getEmail(), mockUser.getPassword())).thenReturn(tokenResponse);

        ResponseEntity<?> response = authController.login(mockUser);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(authService).login(mockUser.getEmail(), mockUser.getPassword());
    }

    @Test
    void testLogin_Failure() {
        // Arrange
        when(authService.login(mockUser.getEmail(), mockUser.getPassword()))
                .thenThrow(new InvalidCredentialsException("Invalid Credentials"));

        // Act
        ResponseEntity<?> response = authController.login(mockUser);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Unauthorized", responseBody.get("error"));
        assertEquals("Invalid Credentials", responseBody.get("message"));

        verify(authService).login(mockUser.getEmail(), mockUser.getPassword());
    }


    @Test
    void testRefreshToken_Success() {
        String email = "test@example.com";
        String refreshToken = "mockRefreshToken";
        when(authService.refreshAccessToken(email, refreshToken)).thenReturn("newAccessToken");

        ResponseEntity<Map<String, String>> response = authController.refreshAccessToken(Map.of(
                "email", email,
                "refreshToken", refreshToken
        ));

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("newAccessToken", response.getBody().get("accessToken"));
        verify(authService).refreshAccessToken(email, refreshToken);
    }

    @Test
    void testLogout() {
        doNothing().when(authService).logout(mockUser.getEmail());

        ResponseEntity<String> response = authController.logout(Map.of("email", mockUser.getEmail()));

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Logged out successfully", response.getBody());
        verify(authService).logout(mockUser.getEmail());
    }

    @Test
    void testGetUserData_Success() {
        UserData mockUserData = new UserData("test@example.com", "USER");
        when(authService.getUserDataFromCache(mockUser.getEmail())).thenReturn(mockUserData);

        ResponseEntity<?> response = authController.getUserData(mockUser.getEmail());

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(authService).getUserDataFromCache(mockUser.getEmail());
    }

    @Test
    void testGetUserData_UserNotFound() {
        when(authService.getUserDataFromCache(mockUser.getEmail())).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> authController.getUserData(mockUser.getEmail()));
        verify(authService).getUserDataFromCache(mockUser.getEmail());
    }
}
