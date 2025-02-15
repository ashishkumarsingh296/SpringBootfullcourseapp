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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    APIKeyRepository apiKeyRepository;

//    @Autowired
//    private JwtUtil jwtUtil;

//    private final RefreshTokenService refreshTokenService;

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public AuthService(JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

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

    public Map<String, String> login(String email, String password) {
        // Check if user data is cached
        UserData cachedUserData = getUserDataFromCache(email);

        if (cachedUserData != null) {
            // If user is found in cache, generate tokens and return
            String accessToken = jwtUtil.generateToken(email);
            String refreshToken = jwtUtil.generateRefreshToken(email);
            refreshTokenService.storeRefreshToken(email, refreshToken);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);
            return tokens;
        }

        // Validate user credentials from the database
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            User user = userOpt.get();

            // Store user data in Redis cache
            UserData userData = new UserData(user.getEmail(), user.getRole().name());
            redisTemplate.opsForValue().set(USER_DATA_KEY + email, userData);

            // Generate JWT tokens
            String accessToken = jwtUtil.generateToken(email);
            String refreshToken = jwtUtil.generateRefreshToken(email);
            refreshTokenService.storeRefreshToken(email, refreshToken);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);
            return tokens;
        }

        // If authentication fails, throw a custom exception
        logger.error("Invalid login attempt for email: {}", email);
        throw new InvalidCredentialsException("Invalid email or password.");
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

    public String refreshAccessToken(String email, String refreshToken) {
        String storedRefreshToken = refreshTokenService.getRefreshToken(email);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new InvalidCredentialsException("Invalid or expired refresh token.");
        }

        return jwtUtil.generateToken(email);
    }

    public void logout(String email) {
        refreshTokenService.deleteRefreshToken(email);
    }
}
