package com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.service;

import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.Entity.ApiKey;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.Entity.User;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.genrater.ApiKeyGenerator;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.cache.UserData;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.enums.UserType;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.exceptionhandling.InvalidCredentialsException;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.exceptionhandling.UserAlreadyExistsException;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.repository.APIKeyRepository;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.repository.UserRepository;

import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    APIKeyRepository apiKeyRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private static final String USER_DATA_KEY = "user_data_";

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User register(User userRequest) {
        User newUser=null;
        try {
            if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
                logger.warn("User already exists: {}", userRequest.getEmail());
                throw new UserAlreadyExistsException("User with email " + userRequest.getEmail() + " already exists.");
            }
            String generatedApiKey = ApiKeyGenerator.generateApiKey();
            newUser = new User();
            newUser.setEmail(userRequest.getEmail());
            newUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            newUser.setRole(userRequest.getRole());
            newUser.setApiKey(generatedApiKey);
            newUser.setUserType(UserType.FREE);
            userRepository.save(newUser);

            // Generate and store API Key
            ApiKey apiKey = ApiKey.builder()
                    .apiKey(generatedApiKey) // Generate API key
                    .user(newUser)
                    .createdAt(LocalDateTime.now())
                    .build();

            apiKeyRepository.save(apiKey);

            logger.info("User registered successfully: {}", userRequest.getEmail());


        } catch (Exception e) {
            logger.error("Error registering user: {}", userRequest.getEmail(), e);
            throw new RuntimeException("An unexpected error occurred during registration.");
        }
        return newUser;
    }

    public String login(String email, String password) {
        try {
            // Check cache for user data
            UserData cachedUserData = getUserDataFromCache(email);
            if (cachedUserData != null) {
                return jwtUtil.generateToken(email);
            }

            // If not found in cache, validate credentials from database
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
                User user = userOpt.get();
                UserData userData = new UserData(user.getEmail(), user.getRole().name());
                redisTemplate.opsForValue().set(USER_DATA_KEY + email, userData);
                return jwtUtil.generateToken(email);
            }

            // Throw custom exception for invalid credentials
            throw new InvalidCredentialsException("Invalid email or password.");
        } catch (InvalidCredentialsException e) {
            // Handle invalid credentials error (Already caught)
            logger.error("Invalid credentials for user: " + email, e);
            throw e; // Re-throw to let the controller handle it (401 Unauthorized)
        } catch (Exception e) {
            // Catch all other unexpected errors
            logger.error("Error during login for user: " + email, e);
            throw new RuntimeException("An unexpected error occurred during login."); // Let the GlobalExceptionHandler catch it
        }
    }


    // Method to get user data from Redis cache
    public UserData getUserDataFromCache(String email) {
        return (UserData) redisTemplate.opsForValue().get(USER_DATA_KEY + email);
    }


    // Scheduled task to update Redis every 3 minutes if data has changed
    @Scheduled(cron = "0 */3 * * * *") // Every 30 minutes
    public void updateUserDataInRedis() {
        Iterable<User> users = userRepository.findAll();
        for (User user : users) {
            UserData currentData = new UserData(user.getEmail(), user.getRole().name());
            UserData cachedData = getUserDataFromCache(user.getEmail());

            // Update cache only if data has changed
            if (cachedData == null || !cachedData.equals(currentData)) {
                redisTemplate.opsForValue().set(USER_DATA_KEY + user.getEmail(), currentData);
                System.out.println("Updated user data for: " + user.getEmail());
            }
        }
    }
}
