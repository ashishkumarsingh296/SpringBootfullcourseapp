package com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.Entity;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;

public class GenerateJwtSecret {
    public static void main(String[] args) {
        byte[] keyBytes = Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded();
        String base64Key = Base64.getEncoder().encodeToString(keyBytes);
        System.out.println("Generated JWT Secret Key: " + base64Key);
    }
}
