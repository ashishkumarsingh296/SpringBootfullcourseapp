//package com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.service;
//
//import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.Entity.User;
//import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.cache.UserData;
//import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.enums.Role;
//import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.exceptionhandling.InvalidCredentialsException;
//import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.repository.UserRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class AuthServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private AuthService authService;
//
////    @InjectMocks
////    private AuthService authService;
//
//    @Test
//    void testRegister_Success() {
//        // Arrange
//        User user = new User();
//        user.setEmail("user@example.com");
//        user.setPassword("password");
//        user.setRole(Role.ADMIN);
//
//        when(userRepository.findByEmail(user.getEmail())).thenReturn(null); // No existing user
//        when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        // Act
//        String response = authService.register(user);
//
//        // Assert
//        Mockito.verify(userRepository).save(Mockito.any(User.class));  // Ensure save() was called
//        assert response.equals("User registered successfully");
//    }
//
////    @Test
//    void testLogin_Success() {
//        // Arrange
//        User user = new User();
//        user.setEmail("test@example.com");
//        user.setPassword("password");
//        user.setRole(Role.ADMIN);
//
//        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
//
//        // Act
//        String result = authService.login(user.getEmail(), user.getPassword());
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.startsWith("mocked-jwt-token"));
//    }
//
////    @Test
//    void testLogin_Failure() {
//        // Arrange
//        when(userRepository.findByEmail("wrong@example.com")).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(InvalidCredentialsException.class, () -> {
//            authService.login("wrong@example.com", "wrongpassword");
//        });
//    }
//}
