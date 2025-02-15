package com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.repository;

import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.Entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface APIKeyRepository extends JpaRepository<ApiKey,Long> {
    Optional<ApiKey> findByApiKey(String apiKey);

}
