package com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.controller;

import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.Entity.User;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.cache.UserData;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.exceptionhandling.InvalidCredentialsException;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;
//to test use commanan in terminal  mvn -Dtest=AuthControllerTest test
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testRegister() {
        // Arrange (व्यवस्था): एक नकली उपयोगकर्ता बनाएं
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        
        when(authService.register(user)).thenReturn("User registered successfully");

        // Act (कार्रवाई)
        String response = authController.register(user).getBody();

        // Assert (सत्यापन)
        Mockito.verify(authService).register(user);
        assert response.equals("User registered successfully");
    }

    @Test
    void testLogin_Success() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");

        when(authService.login(user.getEmail(), user.getPassword())).thenReturn("mocked-jwt-token");

        // Act
        String response = (String) authController.login(user).getBody();

        // Assert
        Mockito.verify(authService).login(user.getEmail(), user.getPassword());
        assert response.equals("mocked-jwt-token");
    }

    @Test
    void testLogin_Failure() {
        // Arrange
        User user = new User();
        user.setEmail("wrong@example.com");
        user.setPassword("wrongpassword");

        when(authService.login(user.getEmail(), user.getPassword())).thenThrow(new InvalidCredentialsException("Invalid Credentials"));

        // Act & Assert
        try {
            authController.login(user);
        } catch (InvalidCredentialsException e) {
            assert e.getMessage().equals("Invalid Credentials");
        }

        Mockito.verify(authService).login(user.getEmail(), user.getPassword());
    }

    @Test
    void testGetUserData_Success() {
        // Arrange
        UserData userData = new UserData("test@example.com", "Test User");
        when(authService.getUserDataFromCache("test@example.com")).thenReturn(userData);

        // Act
        UserData response = (UserData) authController.getUserData("test@example.com").getBody();

        // Assert
        Mockito.verify(authService).getUserDataFromCache("test@example.com");
        assert response != null;
        assert response.getEmail().equals("test@example.com");
    }

    @Test
    void testRegister_NullUser() {
        // Act & Assert
        try {
            authController.register(null);
        } catch (Exception e) {
            assert e instanceof NullPointerException;
        }
    }
    @Test
    void testLogin_NullEmail() {
        // Arrange
        User user = new User();
        user.setEmail(null);
        user.setPassword("password");

        // Act & Assert
        try {
            authController.login(user);
        } catch (Exception e) {
            assert e instanceof NullPointerException;
        }
    }

    @Test
    void testLogin_NullPassword() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword(null);

        // Act & Assert
        try {
            authController.login(user);
        } catch (Exception e) {
            assert e instanceof NullPointerException;
        }
    }
    @Test
    void testGetUserData_UserNotFound() {
        // Arrange
        when(authService.getUserDataFromCache("notfound@example.com")).thenReturn(null);

        // Act & Assert
        try {
            authController.getUserData("notfound@example.com");
        } catch (Exception e) {
            assert e.getMessage().contains("User with email: notfound@example.com does not exist in cache.");
        }

        Mockito.verify(authService).getUserDataFromCache("notfound@example.com");
    }

}
