package com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
//We store refresh tokens in Redis so they can be invalidated when needed.
@Service
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;

    public RefreshTokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void storeRefreshToken(String email, String refreshToken) {
        redisTemplate.opsForValue().set(email, refreshToken, 7, TimeUnit.DAYS);
    }

    public String getRefreshToken(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    public void deleteRefreshToken(String email) {
        redisTemplate.delete(email);
    }
}
