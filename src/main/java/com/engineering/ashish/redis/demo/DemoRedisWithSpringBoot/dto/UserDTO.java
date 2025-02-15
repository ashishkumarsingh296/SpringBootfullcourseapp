package com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.dto;

import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.enums.Role;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.enums.UserType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private Long userId;
    private String email;
    private Role role;
    private UserType userType;
    private String apiKey;
}
