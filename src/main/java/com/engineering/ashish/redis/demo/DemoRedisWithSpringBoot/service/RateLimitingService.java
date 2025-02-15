package com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class RateLimitingService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final int MAX_REQUESTS = 5;
    private static final int WINDOW_DURATION = 60; // 1 minute

    public boolean isAllowed(String clientId) {
        String key = "rate-limit:" + clientId;
        Long currentCount = redisTemplate.opsForValue().increment(key, 1);

        if (currentCount != null && currentCount == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(WINDOW_DURATION));
        }
        // 🔥 Debugging logs
        System.out.println("Client: " + clientId + " | Requests: " + currentCount);
        return currentCount <= MAX_REQUESTS;
    }

//    public boolean isAllowed(String clientId) {
//        String key = "rate-limit:" + clientId;
//        Long currentCount = redisTemplate.opsForValue().increment(key, 1);
//
//        // 🔥 Setting expiration only when count is 1 (to avoid resetting it every request)
//        if (currentCount != null && currentCount == 1) {
//            redisTemplate.expire(key, Duration.ofSeconds(WINDOW_DURATION));
//        }
//
//        // 🔥 Debugging logs to track rate limiting
//        System.out.println("Client: " + clientId + " | Requests: " + currentCount);
//
//        return currentCount <= MAX_REQUESTS;
//    }
}
