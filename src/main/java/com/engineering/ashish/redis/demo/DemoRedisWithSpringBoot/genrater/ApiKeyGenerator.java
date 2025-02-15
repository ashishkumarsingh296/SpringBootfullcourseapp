package com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.genrater;

import java.security.SecureRandom;
import java.util.Base64;

public class ApiKeyGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();
    
    public static String generateApiKey() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
