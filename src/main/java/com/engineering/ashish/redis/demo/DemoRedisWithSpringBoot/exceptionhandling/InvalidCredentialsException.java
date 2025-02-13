package com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.exceptionhandling;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
