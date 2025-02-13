package com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DemoRedisWithSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoRedisWithSpringBootApplication.class, args);
	}

}
