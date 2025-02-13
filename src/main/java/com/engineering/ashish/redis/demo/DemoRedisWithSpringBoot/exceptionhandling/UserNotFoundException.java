package com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.exceptionhandling;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message) {
        super(message);
    }

}
