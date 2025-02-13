package com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.exceptionhandling;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
