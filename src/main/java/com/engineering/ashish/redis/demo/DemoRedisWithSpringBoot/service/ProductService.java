package com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.service;

import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.Entity.Product;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
//@Cacheable → Fetch from cache if available, otherwise query DB and store in cache.
//@CachePut → Save product in DB and update cache immediately.
//@CacheEvict → Remove product from cache when deleted in DB.
//This reduces unnecessary database queries and improves performance. 🚀
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Cacheable(value = "products", key = "#id")
    public Optional<Product> getProductById(Long id) {
        System.out.println("Fetching product from database...");
        return productRepository.findById(id);
    }

    @CachePut(value = "products", key = "#product.id")
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}