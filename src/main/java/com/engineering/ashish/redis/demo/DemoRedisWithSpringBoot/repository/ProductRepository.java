package com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.repository;

import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}